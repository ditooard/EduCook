package com.bangkit2024.educook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit2024.educook.R
import com.bangkit2024.educook.databinding.ItemRowRecipeBinding
import com.bangkit2024.educook.data.response.DetailMenu
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuListAdapter(private var stories: List<DetailMenu>) :
    RecyclerView.Adapter<MenuListAdapter.StoryViewHolder>() {

    private var filteredStories: List<DetailMenu> = stories.toList()
    private var clickCallback: OnStoryClickCallback? = null

    fun setOnStoryClickCallback(callback: OnStoryClickCallback) {
        this.clickCallback = callback
    }

    fun filter(query: String) {
        filteredStories = if (query.isEmpty()) {
            stories
        } else {
            stories.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    interface OnStoryClickCallback {
        fun onStoryClicked(story: DetailMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowRecipeBinding.inflate(inflater, parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(filteredStories[position])
        holder.itemView.setOnClickListener {
            clickCallback?.onStoryClicked(filteredStories[holder.adapterPosition])
        }
    }

    class StoryViewHolder(private val binding: ItemRowRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: DetailMenu) {
            binding.itemStory = story
            binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = filteredStories.size

    companion object {

        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(imageView)
        }

        @JvmStatic
        fun convertDateToFormattedString(dateStr: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            return try {
                val date: Date? = inputFormat.parse(dateStr)
                date?.let { outputFormat.format(it) } ?: ""
            } catch (e: ParseException) {
                e.printStackTrace()
                ""
            }
        }
    }
}
