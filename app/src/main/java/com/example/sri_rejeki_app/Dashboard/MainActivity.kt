package com.example.sri_rejeki_app.Dashboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sri_rejeki_app.databinding.ActivityMainBinding
import com.example.sri_rejeki_app.Fragment.CameraFragment
import com.example.sri_rejeki_app.Profile.ProfileFragment
import com.example.sri_rejeki_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // Prepare Recycle View
    private lateinit var presensiAdapter: PresensiAdapter
    private val listPresensi = mutableListOf<Presensi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)

        // Set nama user
        val username = sharedPref.getString("username", "User")
        binding.tvHalo.text = "Hai, $username!"

        // Setup RecyclerView
        binding.rvRiwayatPresensi.layoutManager = LinearLayoutManager(this)
        presensiAdapter = PresensiAdapter(listPresensi)
        binding.rvRiwayatPresensi.adapter = presensiAdapter

        loadPresensiFromFirebase(username.toString())
        showLoading(false)

        // Kamera Fragment
        binding.ivButtonScan.setOnClickListener {
            val fragment = CameraFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Logout
        binding.btnProfile.setOnClickListener {
            val fragment = ProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun loadPresensiFromFirebase(username: String) {
        val ref = FirebaseDatabase.getInstance().getReference("presensi")
        ref.orderByChild("username").equalTo(username)
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