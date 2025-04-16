package com.example.sri_rejeki_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.sri_rejeki_app.Login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Delay 3 detik, lalu pindah ke MainActivity
        lifecycleScope.launch {
            delay(100)
            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            finish()
        }
    }
}