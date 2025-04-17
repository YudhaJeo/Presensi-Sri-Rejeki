package com.example.sri_rejeki_app.Fragment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.sri_rejeki_app.R
import com.example.sri_rejeki_app.databinding.FragmentCameraBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var isScanned = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Log.e("CameraFragment", "Camera permission denied.")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyzer())
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CameraFragment", "Failed to bind camera use cases", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private inner class QRCodeAnalyzer : ImageAnalysis.Analyzer {
        private val scanner = BarcodeScanning.getClient()

        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null && !isScanned) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            barcode.rawValue?.let { rawValue ->
                                if (rawValue == "presensi_sri_rejeki_trigger") {
                                    isScanned = true

                                    // Ambil data dari shared preferences untuk mengecek login hari ini
                                    val sharedPref = requireActivity().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
                                    val username = sharedPref.getString("username", "unknown") ?: "unknown"
                                    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                                    // Cek login hari ini di Firebase
                                    val databaseRef = FirebaseDatabase.getInstance().getReference("presensi")
                                    databaseRef.orderByChild("username").equalTo(username)
                                        .get()
                                        .addOnSuccessListener { snapshot ->
                                            var sudahLoginHariIni = false

                                            for (dataSnapshot in snapshot.children) {
                                                val waktuFromDb = dataSnapshot.child("waktu").getValue(String::class.java)
                                                if (waktuFromDb?.startsWith(todayDate) == true) {
                                                    sudahLoginHariIni = true
                                                    break
                                                }
                                            }

                                            if (sudahLoginHariIni) {
                                                // Jika sudah login hari ini, beri tahu pengguna
                                                showAlreadyCheckedInDialog()
                                            } else {
                                                // Pindah ke fragment validasi jika belum login
                                                requireActivity().supportFragmentManager.beginTransaction()
                                                    .replace(R.id.fragment_container, ValidasiFragment())
                                                    .addToBackStack(null)
                                                    .commit()
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.e("QRCodeAnalyzer", "Error checking presensi", it)
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.e("QRCodeAnalyzer", "Barcode scan failed", it)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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
}
