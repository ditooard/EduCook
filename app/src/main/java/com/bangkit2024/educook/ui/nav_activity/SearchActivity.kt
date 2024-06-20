package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2024.educook.adapter.RecipeAdapter
import com.bangkit2024.educook.api.RetrofitClient
import com.bangkit2024.educook.data.response.ImageResponse
import com.bangkit2024.educook.data.response.Recipe
import com.bangkit2024.educook.data.response.RecipeResponse
import com.bangkit2024.educook.databinding.ActivitySearchBinding
import com.bangkit2024.educook.ui.DetailRecipeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : Fragment() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: RecipeAdapter
    private lateinit var searchView: SearchView
    private lateinit var firstPageButton: Button
    private lateinit var prevPageButton: Button
    private lateinit var nextPageButton: Button

    private var currentPage = 0
    private var isLoading = false
    private var hasNextPage = true

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeRecyclerView = binding.rvUsers
        progressBar = binding.progressBar3
        searchView = binding.searchView
        firstPageButton = binding.firstPageButton
        prevPageButton = binding.prevPageButton
        nextPageButton = binding.nextPageButton

        setupRecyclerView()

        adapter.setOnItemClickListener { recipe ->
            val intent = Intent(requireContext(), DetailRecipeActivity::class.java).apply {
                putExtra(DetailRecipeActivity.MENU, recipe)
            }
            startActivity(intent)
        }

        // Load initial data
        fetchRecipes()

        // Set up search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchRecipes(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Set up button functionality
        firstPageButton.setOnClickListener {
            if (currentPage != 0) {
                currentPage = 0
                fetchRecipes(reset = true)
            }
        }

        prevPageButton.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchRecipes(reset = true)
            }
        }

        nextPageButton.setOnClickListener {
            if (hasNextPage) {
                currentPage++
                fetchRecipes(reset = true)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(requireContext(), mutableListOf())
        recipeRecyclerView.adapter = adapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )

        recipeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && hasNextPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 20
                    ) { // assuming 20 is the page size
                        fetchRecipes()
                    }
                }
            }
        })
    }

    private fun fetchImage(imageId: String, callback: (String?) -> Unit) {
        RetrofitClient.api.getImages(imageId).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    callback(response.body()?.data?.url)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun fetchRecipes(reset: Boolean = false) {
        if (!isAdded) return // Ensure the fragment is still added

        isLoading = true
        progressBar.visibility = View.VISIBLE

        if (reset) {
            adapter.clearRecipes()
        }

        Log.d("SearchActivity", "Fetching recipes. Current page: $currentPage")

        RetrofitClient.api.getRecipes(currentPage).enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(
                call: Call<RecipeResponse>,
                response: Response<RecipeResponse>
            ) {
                if (!isAdded) return  // Ensure the fragment is still added

                isLoading = false
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val recipeResponse = response.body()
                    if (recipeResponse != null) {
                        Log.d("SearchActivity", "Recipes fetched successfully.")

                        val recipes = recipeResponse.data
                        if (recipes.isEmpty()) {
                            // No data case
                            adapter.clearRecipes()
                            binding.noDataFound.visibility = View.VISIBLE
                        } else {
                            binding.noDataFound.visibility = View.GONE
                            val updatedRecipes = mutableListOf<Recipe>()

                            for (recipe in recipes) {
                                fetchImage(recipe.imageId) { imageUrl ->
                                    recipe.imageUrl = imageUrl
                                    updatedRecipes.add(recipe)
                                    if (updatedRecipes.size == recipes.size) {
                                        if (isAdded) { // Ensure the fragment is still added
                                            adapter.addRecipes(updatedRecipes)
                                            binding.noDataFound.visibility =
                                                if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                                            hasNextPage = recipeResponse.pagination.hasNextPage
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e("SearchActivity", "Empty response body")
                        adapter.clearRecipes()
                        binding.noDataFound.visibility = View.VISIBLE
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SearchActivity", "Error: ${response.code()}, $errorBody")
                    Toast.makeText(
                        requireContext(),
                        "Failed to load recipes, Error ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    adapter.clearRecipes()
                    binding.noDataFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                if (!isAdded) return  // Ensure the fragment is still added

                isLoading = false
                progressBar.visibility = View.GONE

                Log.e("SearchActivity", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Failed to load recipes", Toast.LENGTH_SHORT)
                    .show()
                adapter.clearRecipes()
                binding.noDataFound.visibility = View.VISIBLE
            }
        })
    }

    private fun searchRecipes(query: String) {
        isLoading = true
        progressBar.visibility = View.VISIBLE
        currentPage = 0

        RetrofitClient.api.searchRecipes(query).enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (!isAdded) return

                isLoading = false
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    val updatedRecipes = mutableListOf<Recipe>()

                    if (recipes.isEmpty()) {
                        adapter.clearRecipes()
                        binding.noDataFound.visibility = View.VISIBLE
                    } else {
                        binding.noDataFound.visibility = View.GONE
                        for (recipe in recipes) {
                            fetchImage(recipe.imageId) { imageUrl ->
                                recipe.imageUrl = imageUrl
                                updatedRecipes.add(recipe)
                                if (updatedRecipes.size == recipes.size) {
                                    if (isAdded) { // Ensure the fragment is still added
                                        adapter.clearRecipes() // Clear previous search results
                                        adapter.addRecipes(updatedRecipes)
                                        binding.noDataFound.visibility =
                                            if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to find recipes", Toast.LENGTH_SHORT)
                        .show()
                    adapter.clearRecipes()
                    binding.noDataFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                if (!isAdded) return

                isLoading = false
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to find recipes", Toast.LENGTH_SHORT)
                    .show()
                adapter.clearRecipes()
                binding.noDataFound.visibility = View.VISIBLE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

