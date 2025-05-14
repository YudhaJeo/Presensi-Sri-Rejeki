package com.example.sri_rejeki_app.Login

import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sri_rejeki_app.Dashboard.MainActivity
import com.example.sri_rejeki_app.R
import com.example.sri_rejeki_app.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    private var internetDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase setup
        database = FirebaseDatabase.getInstance().reference

        // Tombol login
        binding.btnLogin.setOnClickListener {
            if (!isInternetAvailable()) {
                showInternetDialog()
                return@setOnClickListener
            }

            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi username dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Menampilkan ProgressBar
            binding.progressBar.visibility = android.view.View.VISIBLE
            binding.frameProgress.visibility = android.view.View.VISIBLE

            val query = database.child("users").orderByChild("username").equalTo(username)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Menyembunyikan ProgressBar
                    binding.progressBar.visibility = android.view.View.GONE

                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val dbPassword = userSnapshot.child("password").getValue(String::class.java)
                            if (password == dbPassword) {
                                val email = userSnapshot.child("email").getValue(String::class.java)
                                val fullname = userSnapshot.child("fullname").getValue(String::class.java)

                                val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                                sharedPref.edit()
                                    .putString("username", username)
                                    .putString("email", email)
                                    .putString("fullname", fullname)
                                    .apply()

                                Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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
                    // Menyembunyikan ProgressBar
                    binding.progressBar.visibility = android.view.View.GONE

                    Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Database error: ${error.message}")
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", null)

        if (!savedUsername.isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showInternetDialog() {
        if (internetDialog?.isShowing == true) return

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_masalah_koneksi, null)

        internetDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true) // ← Ini memungkinkan menutup dialog jika klik di luar
            .create()

        internetDialog?.show()
    }

}
