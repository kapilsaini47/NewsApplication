package com.kapil.android.newsapp.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.newsapp.R
import com.kapil.android.newsapp.adapter.HeadlinesAdapter
import com.kapil.android.newsapp.domain.models.Article
import com.kapil.android.newsapp.onclickInterface.onClickInterface
import com.kapil.android.newsapp.ui.activity.ArticleActivity
import com.kapil.android.newsapp.ui.activity.PolicyActivity
import com.kapil.android.newsapp.util.Const.Companion.QUERY_PAGE_SIZE
import com.kapil.android.newsapp.util.Resource
import com.kapil.android.newsapp.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Headlines() : Fragment(R.layout.fragment_headlines),onClickInterface {

    private lateinit var rvHeadlines: RecyclerView
    private lateinit var headlinesAdapter: HeadlinesAdapter
    val viewModel by viewModels<NewsViewModel>()
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private var url: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

                else -> {
                    showProgressBar()
                }
            }
        })


        //Arguments for passing data
        headlinesAdapter.setOnItemClickListener {
            val intent = Intent(activity, ArticleActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            intent.putExtra("article", bundle)
            url = it.url.toString()
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
                    Log.i("Headlines Fragment", "new Search called")
                }
                viewModel.searchResponse.observe(viewLifecycleOwner, Observer { response->
                    when(response){
                        is Resource.Success ->{
                            hideProgressBar()
                            response.data?.let {newsResponse->
                                Log.i("Headlines Fragment", "New response")
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

                        else -> {
                            showProgressBar()
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
        when(item.itemId){
            R.id.feedback-> {
                val email = "kapil.saini.01999@gmail.com"
                val addresses = email.split("," ).toString()
                val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:$email")
                    intent.putExtra(Intent.EXTRA_EMAIL,addresses)

                if (intent.resolveActivity(requireContext().packageManager)!=null){
                    startActivity(intent)
                }
            }
            R.id.policy-> {
                val intent = Intent(requireContext(),PolicyActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView(){
        headlinesAdapter = HeadlinesAdapter(this)
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

    override fun onItemClick(position: Int, menuItem: MenuItem, item: Article) {
        when(menuItem.itemId){
            R.id.share ->{
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, url)
                startActivity(Intent.createChooser(intent, "Share via"))
            }
            R.id.report ->{
                val email = "kapil.saini.01999@gmail.com"
                val addresses = email.split("," ).toString()
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:$email")
                intent.putExtra(Intent.EXTRA_EMAIL,addresses)

                if (intent.resolveActivity(requireContext().packageManager)!=null){
                    startActivity(intent)
                }
            }

        }
    }

}