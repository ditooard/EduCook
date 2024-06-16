package com.bangkit2024.educook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2024.educook.R
import com.bangkit2024.educook.data.response.Recipe
import com.bumptech.glide.Glide

class RecipeAdapter(
    private val context: Context,
    private val recipes: MutableList<Recipe>
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    private var onItemClickListener: ((Recipe) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.storyImage)
        val titleTextView: TextView = view.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_row_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        Glide.with(context)
            .load(recipe.imageId)
            .into(holder.image)
        holder.titleTextView.text = recipe.title

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(recipe)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun addRecipes(newRecipes: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Recipe) -> Unit) {
        onItemClickListener = listener
    }
}
