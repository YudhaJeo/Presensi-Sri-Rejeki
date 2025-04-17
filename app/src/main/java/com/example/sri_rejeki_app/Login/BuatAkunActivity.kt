package com.example.sri_rejeki_app.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sri_rejeki_app.databinding.ActivityBuatAkunBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BuatAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuatAkunBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Inisialisasi View Binding
        binding = ActivityBuatAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur agar isi tampilan tidak terpotong oleh system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Tombol untuk mengirim data pembuatan akun
        binding.btnCreateAccount.setOnClickListener {
            createAccount()
        }

        // TextView untuk pindah ke halaman login
        binding.tvLoginInstead.setOnClickListener {
            // Pindah ke LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun createAccount() {
        val fullname = binding.etFullName.text.toString().trim()
        val username = binding.etNewUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (fullname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        val userData = hashMapOf(
            "fullname" to fullname,
            "username" to username,
            "email" to email,
            "password" to password
        )

        database.child("users").push()
            .setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show()
                Log.d("BuatAkunActivity", "Data akun berhasil dikirim ke Realtime Database")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal membuat akun: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("BuatAkunActivity", "Error mengirim data akun", e)
            }
    }

}
