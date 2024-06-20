package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2024.educook.adapter.RecipeAdapter
import com.bangkit2024.educook.api.RetrofitClient
import com.bangkit2024.educook.data.response.RecipeUserResponse
import com.bangkit2024.educook.databinding.ActivityProfileBinding
import com.bangkit2024.educook.ui.DetailRecipeActivity
import com.bangkit2024.educook.ui.LoginActivity
import com.bangkit2024.educook.viewmodel.ProfileViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : Fragment() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: RecipeAdapter
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeRecyclerView = binding.rvUsers
        progressBar = binding.progressBar3

        // Set LayoutManager for RecyclerView
        recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RecipeAdapter(requireContext(), mutableListOf())
        recipeRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { recipe ->
            val intent = Intent(requireContext(), DetailRecipeActivity::class.java).apply {
                putExtra(DetailRecipeActivity.MENU, recipe)
            }
            startActivity(intent)
        }

        binding.ivLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.progressBar3.visibility = View.VISIBLE
        Log.d("ProfileActivity", "Progress bar set to VISIBLE")

        lifecycleScope.launch {
            val token = profileViewModel.getToken().first()
            Log.d("ProfileActivity", "Token retrieved: $token")
            profileViewModel.getRecipesByUser(token).observe(viewLifecycleOwner) { result ->
                Log.d("ProfileActivity", "Observer called with result: $result")
                binding.progressBar3.visibility = View.GONE
                result.onSuccess { recipes ->
                    recipes?.let { nonNullRecipes ->
                        fetchAndDisplayRecipes(nonNullRecipes)
                    }
                }.onFailure { error ->
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch recipes: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            profileViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(LoginActivity.EXTRA_LOGOUT_FLAG, true)
            startActivity(intent)
            requireActivity().finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private suspend fun fetchImage(imageId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getImages(imageId).execute()
                if (response.isSuccessful) {
                    response.body()?.data?.url
                } else {
                    Log.e("ProfileActivity", "Failed to fetch image URL: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error fetching image URL", e)
                null
            }
        }
    }

    private fun fetchAndDisplayRecipes(recipes: RecipeUserResponse) {
        lifecycleScope.launch {
            val updatedRecipes = recipes.resep.map { recipe ->
                recipe.apply {
                    imageUrl = fetchImage(recipe.imageId)
                }
            }
            Log.d("ProfileActivity", "Updated recipes: $updatedRecipes")

            // Check if the fragment is still added before updating the UI
            if (isAdded) {
                adapter.addRecipes(updatedRecipes)
                binding.progressBar3.visibility = View.GONE
                Log.d("ProfileActivity", "Recipes displayed and progress bar set to GONE")
                updateNoDataFoundView(updatedRecipes.isEmpty())
            }
        }
    }

    private fun updateNoDataFoundView(isEmpty: Boolean) {
        binding.noDataFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvUsers.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
