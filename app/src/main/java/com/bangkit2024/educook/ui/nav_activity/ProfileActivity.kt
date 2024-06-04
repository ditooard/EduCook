package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bangkit2024.educook.databinding.ActivityProfileBinding
import com.bangkit2024.educook.ui.LoginActivity
import com.bangkit2024.educook.viewmodel.ProfileViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory

class ProfileActivity : Fragment() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(LoginActivity.EXTRA_LOGOUT_FLAG, true)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}