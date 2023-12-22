package com.example.weatherapp.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Preferences()
            1 -> LeftFragment()
            2 -> MainFragment()
            3 -> RightFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}