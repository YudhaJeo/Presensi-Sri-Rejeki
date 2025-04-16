package com.example.sri_rejeki_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.sri_rejeki_app.Login.LoginActivity
import com.example.sri_rejeki_app.databinding.ActivityMainBinding
import com.example.sri_rejeki_app.Fragment.CameraFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)

        // Set nama user
        val username = sharedPref.getString("username", "User")
        binding.tvHalo.text = "Hai, $username!"

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
}

