package com.bangkit2024.educook.ui

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit2024.educook.R
import com.bangkit2024.educook.data.local.BookmarkMenu
import com.bangkit2024.educook.data.response.Recipe
import com.bangkit2024.educook.databinding.ActivityDetailRecipeBinding
import com.bangkit2024.educook.viewmodel.DetailRecipeViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.apply {

            val title = intent.getStringExtra("title")
            val ingredients = intent.getStringExtra("ingredients")
            val directions = intent.getStringExtra("directions")

            tvTitle.text = title
            tvIngredient.text = ingredients
            tvDirections.text = directions

            viewModel = ViewModelProvider(this@DetailRecipeActivity).get(DetailRecipeViewModel::class.java)

            var _Checked = false
            CoroutineScope(Dispatchers.IO).launch {
                val count = menuDetails?.id?.let { viewModel.checkBookmark(it) }
                withContext(Dispatchers.Main) {
                    if (count != null) {
                        _Checked = count > 0
                        ibBookmark.isChecked = _Checked
                    }
                }
            }

            ibBookmark.setOnClickListener {
                _Checked = !_Checked
                if (_Checked) {
                    if (menuDetails?.id != null &&
                        menuDetails?.title != null &&
                        menuDetails?.directions != null &&
                        menuDetails?.ingredients != null &&
                        menuDetails?.createdAt != null &&
                        menuDetails?.updatedAt != null &&
                        menuDetails?.imageId != null &&
                        menuDetails?.idUser != null
                    ) {
                        Log.d("addToBookmark", "success")
                        val bookmarkMenu = BookmarkMenu(
                            menuDetails!!.id,
                            menuDetails!!.title,
                            menuDetails!!.directions,
                            menuDetails!!.ingredients,
                            menuDetails!!.createdAt.toString(),
                            menuDetails!!.updatedAt.toString(),
                            menuDetails!!.imageId,
                            menuDetails!!.idUser,
                        )
                        viewModel.addBookmark(bookmarkMenu)
                    } else {
                        Toast.makeText(
                            this@DetailRecipeActivity,
                            "Menu information is incomplete",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    Toast.makeText(
                        this@DetailRecipeActivity,
                        "Menu Added to your Bookmark",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.d("removeFromBookmark", "success")
                    if (menuDetails?.id != null) {
                        viewModel.removeBookmark(menuDetails!!.id)
                    }
                    Toast.makeText(
                        this@DetailRecipeActivity,
                        "Menu Removed from your Bookmark",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ibBookmark.isChecked = _Checked
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
        Glide.with(this)
            .load(storyDetails.imageId)
            .into(binding.ivPhoto)
    }

    companion object {
        const val MENU = "extra_menu"
    }
}
