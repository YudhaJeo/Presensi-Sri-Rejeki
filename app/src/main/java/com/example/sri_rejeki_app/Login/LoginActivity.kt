package com.example.sri_rejeki_app.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sri_rejeki_app.Login.BuatAkunActivity
import com.example.sri_rejeki_app.MainActivity
import com.example.sri_rejeki_app.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().reference

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi username dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val query = database.child("users").orderByChild("username").equalTo(username)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val dbPassword = userSnapshot.child("password").getValue(String::class.java)
                            if (password == dbPassword) {
                                Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                                // Simpan sesi login
                                val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                                sharedPref.edit().putString("username", username).apply()

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

        binding.tvCreateAccount.setOnClickListener {
            val intent = Intent(this, BuatAkunActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", null)

        if (savedUsername != null) {
            // Sesi masih ada, langsung ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
