package com.example.sri_rejeki_app

import PresensiAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sri_rejeki_app.Login.LoginActivity
import com.example.sri_rejeki_app.databinding.ActivityMainBinding
import com.example.sri_rejeki_app.Fragment.CameraFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var presensiAdapter: PresensiAdapter
    private val listPresensi = mutableListOf<Presensi>()

    private var noInternetDialog: Dialog? = null
    private val internetCheckHandler = Handler()
    private val internetCheckRunnable = object : Runnable {
        override fun run() {
            if (!isInternetAvailable(this@MainActivity)) {
                showNoInternetDialog()
            } else {
                hideNoInternetDialog()
            }
            internetCheckHandler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val fullname = sharedPref.getString("username", "User")
        binding.tvHalo.text = "Hai, $fullname!"

        // Setup RecyclerView
        binding.rvRiwayatPresensi.layoutManager = LinearLayoutManager(this)
        presensiAdapter = PresensiAdapter(listPresensi)
        binding.rvRiwayatPresensi.adapter = presensiAdapter

        loadPresensiFromFirebase(fullname.toString())
        showLoading(false)

        // Kamera Fragment
        binding.ivButtonScan.setOnClickListener {
            val fragment = CameraFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.camera_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            sharedPref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Mulai pengecekan koneksi internet
        internetCheckHandler.post(internetCheckRunnable)
    }

    private fun loadPresensiFromFirebase(fullname: String) {
        val ref = FirebaseDatabase.getInstance().getReference("presensi")
        ref.orderByChild("username").equalTo(fullname)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listPresensi.clear()
                    for (data in snapshot.children) {
                        val item = data.getValue(Presensi::class.java)
                        item?.let { listPresensi.add(it) }
                    }
                    presensiAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showNoInternetDialog() {
        if (noInternetDialog == null) {
            noInternetDialog = Dialog(this)
            noInternetDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            noInternetDialog?.setContentView(R.layout.dialog_masalah_koneksi)
            noInternetDialog?.setCancelable(false)
            noInternetDialog?.window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            noInternetDialog?.findViewById<View>(R.id.btnDialogOK)?.setOnClickListener {
                noInternetDialog?.dismiss()
            }
        }

        if (!noInternetDialog!!.isShowing) {
            noInternetDialog?.show()
        }
    }

    private fun hideNoInternetDialog() {
        noInternetDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        internetCheckHandler.removeCallbacks(internetCheckRunnable)
    }
}
