package com.app.openinapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.openinapp.databinding.ActivityMainBinding
import com.app.openinapp.ui.fragment.CampaignFragment
import com.app.openinapp.ui.fragment.CoursesFragment
import com.app.openinapp.ui.fragment.MainFragment
import com.app.openinapp.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.background = null
        binding.navigationView.menu.getItem(2).isEnabled = false

        binding.navigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.navigation_courses -> {
                    replaceFragment(CoursesFragment(), "Courses")
                    true
                }
                R.id.navigation_campaigns -> {
                    replaceFragment(CampaignFragment(), "Campaigns")
                    true
                }
                R.id.navigation_profile -> {
                    replaceFragment(ProfileFragment(), "Profile")
                    true
                }
                else -> {
                    replaceFragment(MainFragment(), "Dashboard")
                    true
                }
            }
        }
        replaceFragment(MainFragment(), "Dashboard")

    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
        binding.toolbarTitle.text = title

    }

}