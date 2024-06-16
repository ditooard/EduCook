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
import com.bangkit2024.educook.adapter.RecipeAdapter
import com.bangkit2024.educook.api.RetrofitClient
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

    private var currentPage = 1  // Start from page 1
    private var isLoading = false
    private var hasNextPage = true

    private lateinit var binding: ActivitySearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeRecyclerView = binding.rvUsers
        progressBar = binding.progressBar3

        setupRecyclerView()

        adapter.setOnItemClickListener { recipe ->
            val intent = Intent(requireContext(), DetailRecipeActivity::class.java).apply {
                putExtra(DetailRecipeActivity.MENU, recipe)
            }
            startActivity(intent)
        }

        // Load initial data
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

                // Jika pengguna mencapai atau melewati item terakhir yang terlihat
                if (!isLoading && hasNextPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
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

        Log.d("SearchActivity", "Fetching recipes. Current page: $currentPage")

        RetrofitClient.api.getRecipes(currentPage).enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(
                call: Call<RecipeResponse>,
                response: Response<RecipeResponse>
            ) {
                if (!isAdded) return  // Ensure the fragment is still added

                if (response.isSuccessful) {
                    val recipeResponse = response.body()
                    if (recipeResponse != null) {
                        Log.d("SearchActivity", "Recipes fetched successfully.")
                        // Tambahkan data baru ke adapter tanpa mengganti data yang ada
                        adapter.addRecipes(recipeResponse.data)
                        hasNextPage = recipeResponse.pagination.hasNextPage
                        currentPage++
                        Log.d(
                            "SearchActivity",
                            "Next page: $currentPage, hasNextPage: $hasNextPage"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SearchActivity", "Error: ${response.code()}, $errorBody")
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

                Log.e("SearchActivity", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Gagal memuat resep", Toast.LENGTH_SHORT).show()

                isLoading = false
                progressBar.visibility = View.GONE
            }
        })
    }

}
