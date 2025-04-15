package com.example.sri_rejeki_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sri_rejeki_app.databinding.ActivityMainBinding  // Import binding yang dihasilkan
import androidx.fragment.app.FragmentTransaction
import com.example.sri_rejeki_app.Fragment.CameraFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menginisialisasi binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set layout menggunakan root view dari binding
        setContentView(binding.root)

        // Menambahkan listener pada ivButtonScan
        binding.ivButtonScan.setOnClickListener {
            // Menampilkan CameraFragment saat button di klik
            val fragment = CameraFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.camera_fragment, fragment) // Pastikan ada container untuk menampung fragment
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
