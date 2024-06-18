package com.bangkit2024.educook.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit2024.educook.R
import com.bangkit2024.educook.data.local.BookmarkMenu
import com.bangkit2024.educook.data.response.Recipe
import com.bangkit2024.educook.databinding.ActivityDetailRecipeBinding
import com.bangkit2024.educook.viewmodel.DetailRecipeViewModel
import com.bumptech.glide.Glide

class DetailRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRecipeBinding
    private lateinit var viewModel: DetailRecipeViewModel
    private var menuDetails: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuDetails = intent.getSerializableExtra(MENU) as? Recipe

        if (menuDetails == null) {
            Toast.makeText(this, "Recipe data is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        binding.ibArrowBack.setOnClickListener {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.apply {
            // Load the image using Glide
            Glide.with(this@DetailRecipeActivity)
                .load(menuDetails?.imageUrl)
                .placeholder(R.drawable.blank_photo) // Optional placeholder image / Optional error image
                .into(ivPhoto)

            tvTitle.text = menuDetails?.title
            tvIngredient.text = menuDetails?.ingredients
            tvDirections.text = menuDetails?.directions

            viewModel =
                ViewModelProvider(this@DetailRecipeActivity).get(DetailRecipeViewModel::class.java)

            viewModel.checkBookmark(menuDetails?.id!!)
                ?.observe(this@DetailRecipeActivity) { count ->
                    val isChecked = count > 0
                    binding.ibBookmark.isChecked = isChecked
                }

            binding.ibBookmark.setOnClickListener {
                val isChecked = binding.ibBookmark.isChecked
                if (isChecked) {
                    val bookmarkMenu = BookmarkMenu(
                        menuDetails!!.id,
                        menuDetails!!.title,
                        menuDetails!!.directions,
                        menuDetails!!.ingredients,
                        menuDetails!!.createdAt.toString(),
                        menuDetails!!.updatedAt.toString(),
                        menuDetails!!.imageId,
                        menuDetails!!.idUser,
                        menuDetails!!.imageUrl
                    )
                    viewModel.addBookmark(bookmarkMenu)
                    Toast.makeText(
                        this@DetailRecipeActivity,
                        "Menu Added to your Bookmark",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.removeBookmark(menuDetails!!.id)
                    Toast.makeText(
                        this@DetailRecipeActivity,
                        "Menu Removed from your Bookmark",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        displayStoryDetails(menuDetails!!)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun displayStoryDetails(storyDetails: Recipe) {
        binding.apply {
            tvTitle.text = storyDetails.title
            tvIngredient.text = storyDetails.ingredients
            tvDirections.text = storyDetails.directions
        }
        Glide.with(this@DetailRecipeActivity)
            .load(menuDetails?.imageUrl)
            .placeholder(R.drawable.blank_photo) // Optional placeholder image / Optional error image
            .into(binding.ivPhoto)
    }

    companion object {
        const val MENU = "extra_menu"
    }
}