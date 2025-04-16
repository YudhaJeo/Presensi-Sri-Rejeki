package com.example.sri_rejeki_app.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sri_rejeki_app.databinding.FragmentValidasiBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ValidasiFragment : Fragment() {

    private lateinit var binding: FragmentValidasiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentValidasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnKirim.setOnClickListener {
            kirimPresensi()
        }
    }

    private fun kirimPresensi() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "unknown")

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = sdf.format(Date())

        val dataPresensi = mapOf(
            "username" to username,
            "waktu" to timestamp,
            "jenis_presensi" to "sore"
        )

        FirebaseDatabase.getInstance().getReference("presensi")
            .push()
            .setValue(dataPresensi)
            .addOnSuccessListener {
                Log.d("CameraFragment", "Presensi berhasil dikirim")
            }
            .addOnFailureListener {
                Log.e("CameraFragment", "Gagal mengirim presensi", it)
            }
    }
}
