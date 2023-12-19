package com.example.weatherapp.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LeftFragment()
            1 -> MainFragment()
            2 -> RightFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}