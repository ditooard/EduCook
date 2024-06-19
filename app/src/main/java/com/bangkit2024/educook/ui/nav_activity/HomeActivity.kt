package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2024.educook.R
import com.bangkit2024.educook.adapter.RecipeAdapter
import com.bangkit2024.educook.api.RetrofitClient
import com.bangkit2024.educook.data.response.ImageResponse
import com.bangkit2024.educook.data.response.Recipe
import com.bangkit2024.educook.data.response.RecipeResponse
import com.bangkit2024.educook.databinding.ActivityHomeBinding
import com.bangkit2024.educook.ui.AddRecipeActivity
import com.bangkit2024.educook.ui.DetailRecipeActivity
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : Fragment() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: RecipeAdapter

    private lateinit var binding: ActivityHomeBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeRecyclerView = view.findViewById(R.id.rv_users)
        progressBar = view.findViewById(R.id.progressBar3)

        setupRecyclerView()

        binding.btnShare.setOnClickListener {
            val intent = Intent(requireContext(), AddRecipeActivity::class.java)
            startActivity(intent)
        }

        adapter.setOnItemClickListener { recipe ->
            val intent = Intent(requireContext(), DetailRecipeActivity::class.java).apply {
                putExtra(DetailRecipeActivity.MENU, recipe)  // Use the constant key from DetailRecipeActivity
            }
            startActivity(intent)
        }

        fetchRecipes()
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(requireContext(), mutableListOf())
        recipeRecyclerView.adapter = adapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
    }

    private suspend fun fetchImage(imageId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getImages(imageId).execute()
                if (response.isSuccessful) {
                    response.body()?.data?.url
                } else {
                    Log.e("HomeActivity", "Failed to fetch image URL: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error fetching image URL: ${e.message}")
                null
            }
        }
    }

    private fun fetchRecipes() {
        coroutineScope.launch {
            progressBar.visibility = View.VISIBLE

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getRecipes(0).execute()
                }

                if (response.isSuccessful) {
                    val recipeResponse = response.body()
                    if (recipeResponse != null) {
                        Log.d("HomeActivity", "Recipes fetched successfully.")

                        val recipes = recipeResponse.data.take(5) // Ambil 5 data terbaru
                        val updatedRecipes = recipes.map { recipe ->
                            val imageUrl = fetchImage(recipe.imageId)
                            recipe.imageUrl = imageUrl
                            recipe
                        }

                        adapter.addRecipes(updatedRecipes)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("HomeActivity", "Error: ${response.code()}, $errorBody")
                    Toast.makeText(
                        requireContext(),
                        "Gagal memuat resep, Error ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Failure: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memuat resep", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }
}
