package com.bangkit2024.educook.ui.nav_activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit2024.educook.adapter.MenuListAdapter
import com.bangkit2024.educook.data.local.BookmarkMenu
import com.bangkit2024.educook.data.response.DetailMenu
import com.bangkit2024.educook.databinding.ActivityBookmarkBinding
import com.bangkit2024.educook.ui.DetailRecipeActivity
import com.bangkit2024.educook.viewmodel.BookmarkViewModel
import com.bangkit2024.educook.viewmodel.HomeViewModel

class BookmarkActivity : Fragment() {

    private lateinit var binding: ActivityBookmarkBinding
    private lateinit var adapter: MenuListAdapter
    private lateinit var viewModel: BookmarkViewModel

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ActivityBookmarkBinding.inflate(inflater, container, false)

        val adapter = MenuListAdapter(arrayListOf())

        binding.rvUsers.adapter = adapter

        val onStoryClickCallback = object : MenuListAdapter.OnStoryClickCallback {
            override fun onStoryClicked(story: DetailMenu) {
                navigateToDetailRecipeActivity(story)
            }
        }
        adapter.setOnStoryClickCallback(onStoryClickCallback)

        // Tambahkan LinearLayoutManager
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())

        binding.rvUsers.adapter = adapter

        viewModel = ViewModelProvider(this).get(BookmarkViewModel::class.java)

        viewModel.getBookmarkList()?.observe(viewLifecycleOwner, { favoriteUsers ->
            if (favoriteUsers.isNullOrEmpty()) {
                // Menampilkan layout kosong jika daftar favorit kosong
                binding.rvUsers.visibility = View.GONE
            } else {
                // Menampilkan daftar favorit jika ada data
                val bookmarkList = mapList(favoriteUsers)
                adapter.updateData(bookmarkList)

                binding.rvUsers.visibility = View.VISIBLE
                adapter.notifyDataSetChanged() // Memperbarui recyclerview
            }
        })

        return binding.root
    }


    private fun mapList(bookmarkMenus: List<BookmarkMenu>): ArrayList<DetailMenu> {
        val detailMenus = ArrayList<DetailMenu>()
        for (bookmarkMenu in bookmarkMenus) {
            val detailMenu = DetailMenu(
                bookmarkMenu.id,
                bookmarkMenu.name,
                bookmarkMenu.description,
                bookmarkMenu.photoUrl,
                bookmarkMenu.createdAt,
                bookmarkMenu.lat,
                bookmarkMenu.lon
            )
            detailMenus.add(detailMenu)
        }
        return detailMenus
    }


    private fun navigateToDetailRecipeActivity(menu: DetailMenu) {
        val intent = Intent(requireContext(), DetailRecipeActivity::class.java).apply {
            putExtra(DetailRecipeActivity.MENU, menu)
        }
        startActivity(intent)
    }

    companion object
}
