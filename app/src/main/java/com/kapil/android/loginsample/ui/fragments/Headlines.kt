package com.kapil.android.loginsample.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.adapter.HeadlinesAdapter
import com.kapil.android.loginsample.ui.ArticleActivity
import com.kapil.android.loginsample.ui.Home
import com.kapil.android.loginsample.util.Const.Companion.QUERY_PAGE_SIZE
import com.kapil.android.loginsample.util.Resource
import com.kapil.android.loginsample.viewModel.NewsViewModel

class Headlines : Fragment(R.layout.fragment_headlines) {

    private lateinit var rvHeadlines: RecyclerView
    private lateinit var headlinesAdapter: HeadlinesAdapter
    lateinit var viewModel: NewsViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as Home).viewModel

        setHasOptionsMenu(true)
        activity?.title = "Home"
        rvHeadlines = view.findViewById(R.id.rvHeadlines)
        progressBar = view.findViewById(R.id.pbHeadlines)
        setUpRecyclerView()

        viewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let {newsResponse->
                        headlinesAdapter.differ.submitList(newsResponse.articles)
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isAtLastPage = viewModel.headlinesPageNumber == totalPages
                        if (isAtLastPage){
                            rvHeadlines.setPadding(0,0,0,0)
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

        //Arguments for passing data
        headlinesAdapter.setOnItemClickListener {
            val intent = Intent(activity, ArticleActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            intent.putExtra("article", bundle)
            startActivity(intent)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_menu, menu)

        val menuItem: MenuItem = menu.findItem(R.id.search)

        searchView = menuItem.actionView as SearchView
        searchView.queryHint = "Search news..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query?.isNotEmpty() == true){
                    viewModel.searchNews(query.toString())
                }
                viewModel.searchResponse.observe(viewLifecycleOwner, Observer { response->
                    when(response){
                        is Resource.Success ->{
                            hideProgressBar()
                            response.data?.let {newsResponse->
                                headlinesAdapter.differ.submitList(newsResponse.articles)
                                val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                                isAtLastPage = viewModel.searchPageNumber == totalPages
                                if (isAtLastPage){
                                    rvHeadlines.setPadding(0,0,0,0)
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
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.feedback-> {
                val email = "kk4712621@yahoo.com,kk4712621@gmail.com"
                val addresses = email.split("," ).toString()
                val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:$email")
                    intent.putExtra(Intent.EXTRA_EMAIL,addresses)

                if (intent.resolveActivity(requireContext().packageManager)!=null){
                    startActivity(intent)
                }
            }
            R.id.policy-> {
                findNavController().navigate(R.id.action_homeFragment_to_policy)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView(){
        headlinesAdapter = HeadlinesAdapter()
        rvHeadlines.apply {
            adapter = headlinesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@Headlines.scrollListener)
        }
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar(){
        progressBar.visibility = View.INVISIBLE
        isLoading = false
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndAtLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                    && isScrolling
            if(shouldPaginate){
                viewModel.getHeadlines("in")
                viewModel.searchNews(searchView.query.toString())
                isScrolling = false
            }
        }
    }
}