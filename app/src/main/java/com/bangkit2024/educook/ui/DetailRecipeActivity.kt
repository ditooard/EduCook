package com.bangkit2024.educook.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit2024.educook.R
import com.bangkit2024.educook.adapter.MenuListAdapter
import com.bangkit2024.educook.data.response.DetailMenu
import com.bangkit2024.educook.databinding.ActivityDetailRecipeBinding
import com.bumptech.glide.Glide

class DetailRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyDetails = intent.getParcelableExtra<DetailMenu>(STORY) as DetailMenu
        displayStoryDetails(storyDetails)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.apply {
            ibArrowBack.setOnClickListener { finish() }
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
        const val STORY = "story_extra"
    }
}
