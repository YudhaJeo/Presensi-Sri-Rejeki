package com.example.sri_rejeki_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        // Inisialisasi Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Tombol test kirim data
        val testButton: Button = findViewById(R.id.btnTestKirim)
        testButton.setOnClickListener {
            kirimDataTes()
        }
    }

    private fun kirimDataTes() {
        val dataDummy = hashMapOf(
            "nama" to "Yudha",
            "waktu" to System.currentTimeMillis(),
            "status" to "Hadir"
        )

        // Kirim data ke Realtime Database
        database.child("absensi").push()
            .setValue(dataDummy)
            .addOnSuccessListener {
                Log.d("FirebaseTest", "Berhasil dikirim ke Realtime Database")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "Gagal mengirim data", e)
            }
    }
}
