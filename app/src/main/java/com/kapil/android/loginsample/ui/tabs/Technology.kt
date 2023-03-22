package com.kapil.android.loginsample.ui.tabs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.adapter.HeadlinesAdapter
import com.kapil.android.loginsample.ui.ArticleActivity
import com.kapil.android.loginsample.ui.Home
import com.kapil.android.loginsample.util.Const
import com.kapil.android.loginsample.util.Resource
import com.kapil.android.loginsample.viewModel.NewsViewModel

class Technology : Fragment(R.layout.fragment_technology) {
    lateinit var viewModel: NewsViewModel
    lateinit var headlinesAdapter: HeadlinesAdapter
    private lateinit var rvTechnology: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as Home).viewModel

        rvTechnology = view.findViewById(R.id.rvTechnology)
        progressBar = view.findViewById(R.id.progress_bar)
        setUpRecyclerView()

        viewModel.technologyHeadlines.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let {newsResponse->
                        headlinesAdapter.differ.submitList(newsResponse.articles)
                        val totalPages = newsResponse.totalResults / Const.QUERY_PAGE_SIZE + 2
                        isAtLastPage = viewModel.technologyPageNumber == totalPages
                        if (isAtLastPage){
                            rvTechnology.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message.let {
                        Toast.makeText(context, "An error occurred:- ${response.message}",Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

        headlinesAdapter.setOnItemClickListener {
            val intent = Intent(activity, ArticleActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            intent.putExtra("article", bundle)
            startActivity(intent)
        }

    }

    private fun setUpRecyclerView(){
        headlinesAdapter = HeadlinesAdapter()
        rvTechnology.apply {
            adapter = headlinesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@Technology.scrollListener)
        }
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
        isLoading=true
    }

    private fun hideProgressBar(){
        progressBar.visibility = View.INVISIBLE
        isLoading=false
    }

    var isLoading = false
    var isAtLastPage = false
    var isScrolling = false

    var scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndAtLastPage = !isLoading && !isAtLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Const.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndAtLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                    && isScrolling
            if(shouldPaginate){
                viewModel.getTechnologyHeadlines("in")
                isScrolling = false
            }
        }
    }
}