package com.example.sri_rejeki_app.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sri_rejeki_app.Login.LoginActivity
import com.example.sri_rejeki_app.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

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
        val sharedPref = requireActivity().getSharedPreferences("user_session", MODE_PRIVATE)

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            sharedPref.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}
