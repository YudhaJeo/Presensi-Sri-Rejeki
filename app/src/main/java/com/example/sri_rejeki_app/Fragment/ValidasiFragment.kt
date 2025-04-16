package com.example.sri_rejeki_app.Fragment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sri_rejeki_app.databinding.DialogLokasiTidakValidBinding
import com.example.sri_rejeki_app.databinding.FragmentValidasiBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ValidasiFragment : Fragment() {

    private lateinit var binding: FragmentValidasiBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Lokasi tujuan (misalnya kantor atau lokasi absensi) -7.646193, 111.527312
    private val targetLatitude = -7.646193 // Ganti dengan latitude yang sesuai
    private val targetLongitude = 111.527312 // Ganti dengan longitude yang sesuai
    private val radius = 30 // radius dalam meter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentValidasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnKirim.setOnClickListener {
            validasiLokasi()
        }
    }

    private fun validasiLokasi() {
        // Mengecek izin akses lokasi
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            // Ambil lokasi perangkat
            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), OnSuccessListener { location ->
                if (location != null) {
                    val userLatitude = location.latitude
                    val userLongitude = location.longitude

                    // Hitung jarak antara lokasi perangkat dan lokasi target
                    val distance = hitungJarak(userLatitude, userLongitude, targetLatitude, targetLongitude)

                    if (distance <= radius) {
                        // Lokasi valid, lanjutkan kirim presensi
                        kirimPresensi()
                    } else {
                        // Lokasi tidak valid, tampilkan Toast
                        showCustomLocationDialog(distance)
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal mendapatkan lokasi perangkat", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Jika izin akses lokasi belum diberikan
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun hitungJarak(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun kirimPresensi() {
        showLoading(true)

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
                showLoading(false)

                Log.d("ValidasiFragment", "Presensi berhasil dikirim")
                Toast.makeText(requireContext(), "Presensi berhasil dikirim", Toast.LENGTH_SHORT).show()

                // Menutup fragment setelah presensi dikirim
                requireActivity().supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            .addOnFailureListener {
                showLoading(false)

                Log.e("ValidasiFragment", "Gagal mengirim presensi", it)
            }
    }

    private fun showCustomLocationDialog(distance: Float) {
        val bindingDialog = DialogLokasiTidakValidBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).setView(bindingDialog.root).create()

        bindingDialog.tvDialogMessage.text =
            "Anda berada di luar radius absensi.\nJarak saat ini: ${"%.2f".format(distance)} meter."

        bindingDialog.btnDialogOK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }



}
