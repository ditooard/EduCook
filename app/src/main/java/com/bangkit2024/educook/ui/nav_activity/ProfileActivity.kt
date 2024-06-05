package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit2024.educook.adapter.MenuListAdapter
import com.bangkit2024.educook.data.local.UserPreference
import com.bangkit2024.educook.data.local.dataStore
import com.bangkit2024.educook.data.response.DetailMenu
import com.bangkit2024.educook.databinding.ActivityProfileBinding
import com.bangkit2024.educook.ui.DetailRecipeActivity
import com.bangkit2024.educook.ui.LoginActivity
import com.bangkit2024.educook.viewmodel.HomeViewModel
import com.bangkit2024.educook.viewmodel.ProfileViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class ProfileActivity : Fragment() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userToken: String

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private val profileViewModel by viewModels<ProfileViewModel>{
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ActivityProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        initializeUserPreferences()
        setupObservers()

        binding.ivLogout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(LoginActivity.EXTRA_LOGOUT_FLAG, true)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.layoutManager = layoutManager
        binding.rvUsers.addItemDecoration(
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        )
    }

    private fun initializeUserPreferences() {
        val preferences = UserPreference.getInstance(requireContext().dataStore)

        lifecycleScope.launch {
            preferences.getToken().collect { token ->
                userToken = token
                homeViewModel.fetchStories(userToken)
            }

        }
    }

    private fun setupObservers() {
        homeViewModel.message.observe(viewLifecycleOwner) { message ->
            displayStories(homeViewModel.stories.value ?: emptyList())
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleLoadingIndicator(isLoading)
        }
    }

    private fun displayStories(stories: List<DetailMenu>) {
        toggleNoDataMessage(stories.isEmpty())

        val adapter = MenuListAdapter(stories)
        binding.rvUsers.adapter = adapter

        adapter.setOnStoryClickCallback(object : MenuListAdapter.OnStoryClickCallback {
            override fun onStoryClicked(story: DetailMenu) {
                adapter.setOnStoryClickCallback(object : MenuListAdapter.OnStoryClickCallback {
                    override fun onStoryClicked(story: DetailMenu) {
                        navigateToDetailRecipeActivity(story)
                    }
                })
            }
        })
    }

    private fun navigateToDetailRecipeActivity(story: DetailMenu) {
        val intent = Intent(requireContext(), DetailRecipeActivity::class.java).apply {
            putExtra(DetailRecipeActivity.STORY, story)
        }
        startActivity(intent)
    }

    private fun toggleNoDataMessage(isEmpty: Boolean) {
        binding.noDataFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun toggleLoadingIndicator(isVisible: Boolean) {
        binding.progressBar3.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object
}
