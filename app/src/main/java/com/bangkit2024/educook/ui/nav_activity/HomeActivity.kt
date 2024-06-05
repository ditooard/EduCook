package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit2024.educook.adapter.MenuListAdapter
import com.bangkit2024.educook.databinding.ActivityHomeBinding
import com.bangkit2024.educook.data.local.UserPreference
import com.bangkit2024.educook.data.local.dataStore
import com.bangkit2024.educook.data.response.DetailMenu
import com.bangkit2024.educook.ui.AddRecipeActivity
import com.bangkit2024.educook.ui.DetailRecipeActivity
import com.bangkit2024.educook.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeActivity : Fragment() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userToken: String

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ActivityHomeBinding.inflate(inflater, container, false)
        setupRecyclerView()
        initializeUserPreferences()
        setupObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShare.setOnClickListener {
            val intent = Intent(requireContext(), AddRecipeActivity::class.java)
            startActivity(intent)
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
                navigateToDetailRecipeActivity(story)
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
