package com.bangkit2024.educook.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit2024.educook.adapter.RecipeAdapter
import com.bangkit2024.educook.api.RetrofitClient
import com.bangkit2024.educook.data.response.RecommendResponse
import com.bangkit2024.educook.databinding.ActivityRecommendationBinding
import com.bangkit2024.educook.ui.nav_activity.CameraActivity
import com.bangkit2024.educook.viewmodel.RecommendViewModel
import com.bangkit2024.educook.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecommendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationBinding
    private lateinit var adapter: RecipeAdapter
    private val viewModel by viewModels<RecommendViewModel>{
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(this, mutableListOf())
        binding.rvUsers.adapter = adapter

        binding.ibArrowBack.setOnClickListener { finish() }

        adapter.setOnItemClickListener { recipe ->
            val intent = Intent(this, DetailRecipeActivity::class.java).apply {
                putExtra(DetailRecipeActivity.MENU, recipe)
            }
            startActivity(intent)
        }

        val prediction = intent.getStringExtra(CameraActivity.EXTRA_PREDICT)
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            if (prediction != null) {
                viewModel.getRecipesByPrediction(prediction).observe(this@RecommendationActivity) { result ->
                    binding.progressBar.visibility = View.GONE
                    result.onSuccess { recipes ->
                        recipes?.let { nonNullRecipes ->
                            fetchAndDisplayRecipes(nonNullRecipes)
                        }
                    }.onFailure { error ->
                        Toast.makeText(this@RecommendationActivity, "Failed to fetch recipes: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private suspend fun fetchImage(imageId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getImages(imageId).execute()
                if (response.isSuccessful) {
                    response.body()?.data?.url
                } else {
                    Log.e("RecommendationActivity", "Failed to fetch image URL: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("RecommendationActivity", "Error fetching image URL: ${e.message}")
                null
            }
        }
    }

    private fun fetchAndDisplayRecipes(recipes: RecommendResponse) {
        lifecycleScope.launch {
            val updatedRecipes = recipes.resep.map { recipe ->
                recipe.apply {
                    imageUrl = fetchImage(recipe.imageId)
                }
            }
            adapter.addRecipes(updatedRecipes)
            binding.progressBar.visibility = View.GONE
        }
    }
}