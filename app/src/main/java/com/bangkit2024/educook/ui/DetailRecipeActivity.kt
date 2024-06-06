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

        val menuDetails = intent.getParcelableExtra<DetailMenu>(MENU) as DetailMenu
        displayStoryDetails(menuDetails)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.apply {
            ibArrowBack.setOnClickListener { finish() }

            val name = intent.getStringExtra(NAME)
            val id = intent.getStringExtra(ID)
            val photoUrl = intent.getStringExtra(PHOTO_URL)
            val description = intent.getStringExtra(DESCRIPTION)
            val createdAt = intent.getStringExtra(CREATED_AT)
            val lat = intent.getDoubleExtra(LAT, 0.0)
            val lon = intent.getDoubleExtra(LON, 0.0)

            viewModel = ViewModelProvider(this@DetailRecipeActivity).get(DetailRecipeViewModel::class.java)

            var _Checked = false
            CoroutineScope(Dispatchers.IO).launch {
                val count = id?.let { viewModel.checkBookmark(it) }
                withContext(Dispatchers.Main) {
                    if (count != null) {
                        _Checked = count > 0
                        binding.ibBookmark.isChecked = _Checked
                    }
                }
            }

            binding.ibBookmark.setOnClickListener {
                _Checked = !_Checked
                if (_Checked) {
                    if (id != null && name != null && photoUrl != null && description != null && createdAt != null) {
                        Log.d("addToBookmark", "success")
                        val bookmarkMenu = BookmarkMenu(id, name, description, photoUrl, createdAt, lat, lon)
                        viewModel.addBookmark(bookmarkMenu)
                    }
                    Toast.makeText(this@DetailRecipeActivity, "Menu Added to your Bookmark", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("removeFromBookmark", "success")
                    if (id != null) {
                        viewModel.removeBookmark(id)
                    }
                    Toast.makeText(this@DetailRecipeActivity, "Menu Removed from your Bookmark", Toast.LENGTH_SHORT).show()
                }
                binding.ibBookmark.isChecked = _Checked
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
            .placeholder(R.drawable.ic_launcher_foreground)
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
