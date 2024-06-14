package com.bangkit2024.educook.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit2024.educook.R
import com.bangkit2024.educook.adapter.MenuListAdapter
import com.bangkit2024.educook.data.local.BookmarkMenu
import com.bangkit2024.educook.data.response.DetailMenu
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan data dari intent
        val menuDetails = intent.getParcelableExtra<DetailMenu>(MENU)
        if (menuDetails == null) {
            // Menampilkan pesan kesalahan jika data tidak tersedia
            Toast.makeText(
                this@DetailRecipeActivity,
                "Menu information is incomplete",
                Toast.LENGTH_SHORT
            ).show()
            // Finish activity jika data tidak lengkap
            finish()
            return
        }

        displayStoryDetails(menuDetails)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.apply {
            ibArrowBack.setOnClickListener { finish() }

            viewModel = ViewModelProvider(this@DetailRecipeActivity).get(DetailRecipeViewModel::class.java)

            var _Checked = false
            CoroutineScope(Dispatchers.IO).launch {
                val count = menuDetails.id?.let { viewModel.checkBookmark(it) }
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
                    if (menuDetails.id != null &&
                        menuDetails.name != null &&
                        menuDetails.photoUrl != null &&
                        menuDetails.description != null &&
                        menuDetails.createdAt != null
                    ) {
                        Log.d("addToBookmark", "success")
                        val bookmarkMenu = BookmarkMenu(
                            menuDetails.id,
                            menuDetails.name,
                            menuDetails.description,
                            menuDetails.photoUrl,
                            menuDetails.createdAt,
                            menuDetails.lat,
                            menuDetails.lon
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
                    if (menuDetails.id != null) {
                        viewModel.removeBookmark(menuDetails.id)
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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun displayStoryDetails(storyDetails: DetailMenu) {
        binding.apply {
            tvUsername.text = storyDetails.name
            tvIngredient.text = storyDetails.description
            tvDirections.text = MenuListAdapter.convertDateToFormattedString(storyDetails.createdAt)
        }
        Glide.with(this)
            .load(storyDetails.photoUrl)
            .into(binding.ivPhoto)
    }

    companion object {
        const val MENU = "extra_menu"
        const val NAME = "extra_name"
        const val ID = "extra_id"
        const val PHOTO_URL = "extra_photo_url"
        const val DESCRIPTION = "extra_description"
        const val CREATED_AT = "extra_created_at"
        const val LAT = "extra_lat"
        const val LON = "extra_lon"
    }
}
