package com.example.sri_rejeki_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sri_rejeki_app.databinding.ActivityMainBinding
import androidx.fragment.app.FragmentTransaction
import com.example.sri_rejeki_app.Fragment.CameraFragment
import com.example.sri_rejeki_app.Login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

//    Data karyawan dari Login
    val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
    val username = sharedPref.getString("username", "")
    val email = sharedPref.getString("email", "")
    val fullName = sharedPref.getString("fullName", "")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menginisialisasi binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set layout menggunakan root view dari binding
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Menambahkan listener pada ivButtonScan
        binding.ivButtonScan.setOnClickListener {
            // Menampilkan CameraFragment saat button di klik
            val fragment = CameraFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.camera_fragment, fragment) // Pastikan ada container untuk menampung fragment
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut() // hapus sesi login dari Firebase
            // Hapus sesi login di SharedPreferences
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            sharedPref.edit().clear().apply() // menghapus data session yang tersimpan

            // Pindah ke LoginActivity setelah logout
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}
