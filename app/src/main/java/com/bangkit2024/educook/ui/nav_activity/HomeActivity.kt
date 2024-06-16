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
import com.bangkit2024.educook.data.response.RecipeResponse
import com.bangkit2024.educook.databinding.ActivityHomeBinding
import com.bangkit2024.educook.ui.AddRecipeActivity
import com.bangkit2024.educook.ui.DetailRecipeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : Fragment() {

    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: RecipeAdapter

    private var currentPage = 0
    private var isLoading = false
    private var hasNextPage = true

    private lateinit var binding: ActivityHomeBinding

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

    private fun fetchRecipes() {
        if (!isAdded) return  // Ensure the fragment is still added

        isLoading = true
        progressBar.visibility = View.VISIBLE

        Log.d("HomeActivity", "Fetching recipes. Current page: $currentPage")

        RetrofitClient.api.getRecipes(currentPage).enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(
                call: Call<RecipeResponse>,
                response: Response<RecipeResponse>
            ) {
                if (!isAdded) return  // Ensure the fragment is still added

                if (response.isSuccessful) {
                    val recipeResponse = response.body()
                    if (recipeResponse != null) {
                        Log.d("HomeActivity", "Recipes fetched successfully.")
                        adapter.addRecipes(recipeResponse.data)
                        hasNextPage = recipeResponse.pagination.hasNextPage
                        currentPage++
                        Log.d("HomeActivity", "Next page: $currentPage, hasNextPage: $hasNextPage")
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

                isLoading = false
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                if (!isAdded) return  // Ensure the fragment is still added

                Log.e("HomeActivity", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Gagal memuat resep", Toast.LENGTH_SHORT).show()

                isLoading = false
                progressBar.visibility = View.GONE
            }
        })
    }
}
