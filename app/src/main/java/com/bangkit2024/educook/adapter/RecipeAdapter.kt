package com.bangkit2024.educook.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2024.educook.R
import com.bangkit2024.educook.data.response.Recipe
import com.bumptech.glide.Glide
import android.widget.Filter
import android.widget.Filterable

class RecipeAdapter(
    private val context: Context,
    private val recipes: MutableList<Recipe>
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>(), Filterable {

    private var filteredRecipes: MutableList<Recipe> = recipes.toMutableList()
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
        val recipe = filteredRecipes[position]
        Glide.with(context)
            .load(recipe.imageUrl)
            .placeholder(R.drawable.blank_photo)
            .into(holder.image)
        holder.titleTextView.text = recipe.title

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(recipe)
        }
    }

    override fun getItemCount(): Int {
        return filteredRecipes.size
    }

    fun addRecipes(newRecipes: List<Recipe>?) {
        if (newRecipes != null) {
            recipes.clear()
            recipes.addAll(newRecipes)
            filteredRecipes = recipes.toMutableList()
            notifyDataSetChanged()
        } else {
            Log.e("RecipeAdapter", "New recipes list is null.")
        }
    }

    fun clearRecipes() {
        recipes.clear()
        filteredRecipes.clear()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Recipe) -> Unit) {
        onItemClickListener = listener
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""
                val filteredList = if (query.isEmpty()) {
                    recipes
                } else {
                    recipes.filter {
                        it.title.lowercase().contains(query) ||
                                it.ingredients.lowercase().contains(query) ||
                                it.directions.lowercase().contains(query)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredRecipes = results?.values as MutableList<Recipe>
                notifyDataSetChanged()
            }
        }
    }
}
