package com.example.sri_rejeki_app.Login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sri_rejeki_app.R
import com.example.sri_rejeki_app.databinding.ActivityBuatAkunBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BuatAkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuatAkunBinding
    private lateinit var database: DatabaseReference
    private var internetDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        binding = ActivityBuatAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().reference

        binding.btnCreateAccount.setOnClickListener {
            if (!isConnected()) {
                showInternetDialog()
                return@setOnClickListener
            }
            createAccount()
        }

        binding.tvLoginInstead.setOnClickListener {
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
            Toast.makeText(this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Password harus memiliki minimal 8 karakter, mengandung huruf besar, angka, dan karakter khusus", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEmailValid(email)) {
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
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

    private fun isPasswordStrong(password: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$".toRegex()
        return password.matches(regex)
    }

    private fun isEmailValid(email: String): Boolean {
        val regex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$".toRegex()
        return email.matches(regex)
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showInternetDialog() {
        if (internetDialog?.isShowing == true) return

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_masalah_koneksi, null)

        internetDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true) // Bisa ditutup dengan klik di luar
            .create()

        internetDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        internetDialog?.dismiss()
    }
}
