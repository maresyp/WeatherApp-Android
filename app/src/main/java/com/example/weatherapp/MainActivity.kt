package com.example.weatherapp

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.fragments.ViewPagerAdapter
class MainActivity : FragmentActivity() {

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(this)
        // Start on the main screen
        viewPager.setCurrentItem(2, false)
    }
}