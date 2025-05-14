package com.example.sri_rejeki_app.Profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sri_rejeki_app.Login.LoginActivity
import com.example.sri_rejeki_app.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPref = requireActivity().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)

        // ambil data user dari shared preferences
        val username = sharedPref.getString("username", "unknown")
        val email = sharedPref.getString("email", "unknown")
        val fullname = sharedPref.getString("fullname", "unknown")

        // tampilkan ke layout
        binding.tvNamaLengkap.text = fullname
        binding.tvEmail.text = email
        binding.tvUsername.text = username

        binding.btnLogout.setOnClickListener {
            showLogoutDalog()
        }

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun showLogoutDalog() {
        val bindingDialog = com.example.sri_rejeki_app.databinding.DialogLogoutBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).setView(bindingDialog.root).create()

        bindingDialog.btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        bindingDialog.btnDialogLogout.setOnClickListener {
            firebaseAuth.signOut()
            sharedPref.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}