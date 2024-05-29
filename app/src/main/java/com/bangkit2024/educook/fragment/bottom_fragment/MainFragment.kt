package com.bangkit2024.educook.fragment.bottom_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bangkit2024.educook.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> {
                    replaceFragment(HomeFragment())
                    activity?.title = "Home"
                }

                R.id.bottom_search -> {
                    replaceFragment(SearchFragment())
                    activity?.title = "Search"
                }

                R.id.bottom_bookmark -> {
                    replaceFragment(BookmarkFragment())
                    activity?.title = "Bookmark"
                }

                R.id.bottom_profile -> {
                    replaceFragment(ProfileFragment())
                    activity?.title = "Profile"
                }
            }
            true
        }

        replaceFragment(HomeFragment())
        activity?.title = "Home"
        bottomNavigationView.selectedItemId = R.id.bottom_home

        val addFab = view.findViewById<FloatingActionButton>(R.id.addFabBtn)
        addFab.setOnClickListener {
            Toast.makeText(context, "Add Clicked", Toast.LENGTH_LONG).show()
        }
        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment)
            .commit()
    }

}