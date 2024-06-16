package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit2024.educook.adapter.RecipeAdapter
import com.bangkit2024.educook.data.local.BookmarkMenu
import com.bangkit2024.educook.data.response.Recipe
import com.bangkit2024.educook.databinding.ActivityBookmarkBinding
import com.bangkit2024.educook.ui.DetailRecipeActivity
import com.bangkit2024.educook.viewmodel.BookmarkViewModel
import java.text.SimpleDateFormat
import java.util.*

class BookmarkActivity : Fragment() {

    private lateinit var binding: ActivityBookmarkBinding
    private lateinit var adapter: RecipeAdapter
    private lateinit var viewModel: BookmarkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ActivityBookmarkBinding.inflate(inflater, container, false)

        adapter = RecipeAdapter(requireContext(), arrayListOf())

        binding.rvUsers.adapter = adapter
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(this).get(BookmarkViewModel::class.java)

        viewModel.getBookmarkList()?.observe(viewLifecycleOwner, { favoriteUsers ->
            if (favoriteUsers.isNullOrEmpty()) {
                binding.rvUsers.visibility = View.GONE
            } else {
                val bookmarkList = mapList(favoriteUsers)
                adapter.addRecipes(bookmarkList)
                binding.rvUsers.visibility = View.VISIBLE
            }
        })

//        adapter.setOnItemClickListener { recipe ->
//            navigateToDetailRecipeActivity(recipe)
//        }

        return binding.root
    }

    private fun mapList(bookmarkMenus: List<BookmarkMenu>): ArrayList<Recipe> {
        val detailMenus = ArrayList<Recipe>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        for (bookmarkMenu in bookmarkMenus) {
            val recipe = Recipe(
                id = bookmarkMenu.id,
                title = bookmarkMenu.title,
                directions = bookmarkMenu.directions,
                ingredients = bookmarkMenu.ingredients,
                createdAt = dateFormat.parse(bookmarkMenu.createdAt) ?: Date(),
                updatedAt = dateFormat.parse(bookmarkMenu.updatedAt) ?: Date(),
                imageId = bookmarkMenu.imageId ?: "",
                idUser = bookmarkMenu.idUser ?: ""
            )
            detailMenus.add(recipe)
        }
        return detailMenus
    }

//    private fun navigateToDetailRecipeActivity(menu: Recipe) {
//        val intent = Intent(requireContext(), DetailRecipeActivity::class.java).apply {
//            putExtra(DetailRecipeActivity.MENU, menu)
//        }
//        startActivity(intent)
//    }
}
