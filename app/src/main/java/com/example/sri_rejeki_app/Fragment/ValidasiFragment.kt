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

    // Lokasi tujuan (misalnya kantor atau lokasi absensi) -7.647303, 111.526791
    private val targetLatitude = -7.647303 // Ganti dengan latitude yang sesuai
    private val targetLongitude = 111.526791 // Ganti dengan longitude yang sesuai
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

        val sharedPref = requireActivity().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "User") // Default "User" kalau null

        // Set sapaan
        binding.tvSapa.text = "Hai, $username!"

        // Set waktu saat ini
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val waktu = sdf.format(Date())
        binding.tvTime.text = waktu

        binding.btnKirim.setOnClickListener {
            validasiLokasi()
        }
    }

    private fun validasiLokasi() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                if (location != null) {
                    val userLatitude = location.latitude
                    val userLongitude = location.longitude

                    val distance = hitungJarak(userLatitude, userLongitude, targetLatitude, targetLongitude)

                    if (distance <= radius) {
                        // Lokasi valid → kirim presensi
                        kirimPresensi()
                    } else {
                        // Lokasi di luar radius → hanya tampilkan dialog, tidak kirim presensi
                        showCustomLocationDialog(distance)
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal mendapatkan lokasi perangkat", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
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
        val username = sharedPref.getString("username", "unknown") ?: "unknown"

        val sdfFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sdfDateOnly = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdfDateOnly.format(Date())

        val databaseRef = FirebaseDatabase.getInstance().getReference("presensi")

        // Ambil semua data presensi dan lakukan pengecekan
        databaseRef.get().addOnSuccessListener { snapshot ->
            var sudahPresensi = false

            for (dataSnapshot in snapshot.children) {
                val userFromDb = dataSnapshot.child("username").getValue(String::class.java)
                val waktuFromDb = dataSnapshot.child("waktu").getValue(String::class.java)

                if (userFromDb == username && waktuFromDb?.startsWith(todayDate) == true) {
                    sudahPresensi = true
                    break
                }
            }

            if (sudahPresensi) {
                showLoading(false)
                showAlreadyCheckedInDialog()

            } else {
                // Belum presensi → kirim data baru
                val waktuSekarang = sdfFull.format(Date())

                val dataPresensi = mapOf(
                    "username" to username,
                    "waktu" to waktuSekarang,
                    "jenis_presensi" to "Presensi Berhasil"
                )

                databaseRef.push()
                    .setValue(dataPresensi)
                    .addOnSuccessListener {
                        showLoading(false)
                        showSuccessCheckInDialogAndClose()
                    }
                    .addOnFailureListener {
                        showLoading(false)
                        Toast.makeText(requireContext(), "Gagal mengirim presensi", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(requireContext(), "Gagal memeriksa presensi", Toast.LENGTH_SHORT).show()
            Log.e("ValidasiFragment", "Error checking presensi", it)
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

    private fun showAlreadyCheckedInDialog() {
        val bindingDialog = com.example.sri_rejeki_app.databinding.DialogSudahPresensiBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).setView(bindingDialog.root).create()

        bindingDialog.btnDialogOK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessCheckInDialogAndClose() {
        val bindingDialog = com.example.sri_rejeki_app.databinding.DialogBerhasilPresensiBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).setView(bindingDialog.root).create()

        bindingDialog.btnDialogOK.setOnClickListener {
            dialog.dismiss()
            // Tutup semua fragment langsung jika user pencet OK
            requireActivity().supportFragmentManager.popBackStack(
                null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        // Tutup otomatis setelah 3 detik
        android.os.Handler().postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            requireActivity().supportFragmentManager.popBackStack(
                null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }, 2000)
    }




}
