package com.example.sri_rejeki_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sri_rejeki_app.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    // Inisialisasi View Binding dan Firebase Database Reference
    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Tombol Login ditekan
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi input agar tidak kosong
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi username dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Query untuk mencari user berdasarkan username
            val query = database.child("users").orderByChild("username").equalTo(username)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Asumsikan username bersifat unik, sehingga hanya ada satu data
                        for (userSnapshot in snapshot.children) {
                            val dbPassword = userSnapshot.child("password").getValue(String::class.java)
                            if (password == dbPassword) {
                                Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                                // Navigasi ke MainActivity jika login benar
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Password salah", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Akun tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Database error: ${error.message}")
                }
            })
        }

        // Trigger pindah ke halaman Create New Account
        binding.tvCreateAccount.setOnClickListener {
            val intent = Intent(this, BuatAkunActivity::class.java)
            startActivity(intent)
        }
    }
}
