package com.example.sri_rejeki_app

import PresensiAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
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

//    Prepare Recycle View
    private lateinit var presensiAdapter: PresensiAdapter
    private val listPresensi = mutableListOf<Presensi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //        animasi loading
//        showLoading(true)

        firebaseAuth = FirebaseAuth.getInstance()

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)

        // Set nama user
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
//                    showLoading(false) // <--- pindahkan ke sini
                }

                override fun onCancelled(error: DatabaseError) {
//                    showLoading(false) // tetap jaga ini juga
                }
            })
    }


    //    Progress bar
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

