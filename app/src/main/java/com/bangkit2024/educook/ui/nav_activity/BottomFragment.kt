package com.bangkit2024.educook.ui.nav_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit2024.educook.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BottomFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view =  inflater.inflate(R.layout.fragment_bottom, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bottom_home -> {
                    replaceFragment(HomeActivity())
                    activity?.title = "Home"
                }
                R.id.bottom_search -> {
                    replaceFragment(SearchActivity())
                    activity?.title = "Search"
                }
                R.id.bottom_bookmark -> {
                    replaceFragment(BookmarkActivity())
                    activity?.title = "Bookmark"
                }
                R.id.bottom_profile -> {
                    replaceFragment(ProfileActivity())
                    activity?.title = "Profile"
                }
            }
            true
        }

        replaceFragment(HomeActivity())
        activity?.title = "Home"
        bottomNavigationView.selectedItemId = R.id.bottom_home

        val addFab = view.findViewById<FloatingActionButton>(R.id.addFabBtn)
        addFab.setOnClickListener {
            replaceFragment(CameraActivity())
            activity?.title = "Camera"
        }
        return view
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment,fragment)
            .commit()
    }
}
