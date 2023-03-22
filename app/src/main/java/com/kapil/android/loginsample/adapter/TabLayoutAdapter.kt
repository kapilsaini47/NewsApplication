package com.kapil.android.loginsample.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kapil.android.loginsample.ui.tabs.*

class TabLayoutAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> Business()
            1-> Entertainment()
            2-> Sports()
            3-> General()
            4-> Health()
            5-> Technology()
            6-> Science()
            else -> Business()
        }
    }
}