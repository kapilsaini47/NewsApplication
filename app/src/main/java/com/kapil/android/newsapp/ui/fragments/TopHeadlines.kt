package com.kapil.android.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.kapil.android.newsapp.R
import com.kapil.android.newsapp.adapter.HeadlinesAdapter
import com.kapil.android.newsapp.adapter.TabLayoutAdapter
import com.kapil.android.newsapp.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopHeadlines : Fragment(R.layout.fragment_top_headlines) {

    // var viewModel: NewsViewModel
    val viewModel by viewModels<NewsViewModel>()
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var tabLayoutAdapter: TabLayoutAdapter
    private lateinit var headlinesAdapter: HeadlinesAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = (activity as Home).viewModel

        activity?.title = "Top Headlines"
        tabLayoutAdapter = TabLayoutAdapter(childFragmentManager, lifecycle)
        viewPager2 = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tablayout)
        viewPager2.adapter = tabLayoutAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}