package com.kapil.android.newsapp.ui.tabs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.newsapp.R
import com.kapil.android.newsapp.adapter.HeadlinesAdapter
import com.kapil.android.newsapp.domain.models.Article
import com.kapil.android.newsapp.onclickInterface.onClickInterface
import com.kapil.android.newsapp.ui.activity.ArticleActivity
import com.kapil.android.newsapp.util.Const
import com.kapil.android.newsapp.util.Resource
import com.kapil.android.newsapp.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class General : Fragment(R.layout.fragment_general), onClickInterface {
    // var viewModel: NewsViewModel
    val viewModel by viewModels<NewsViewModel>()
    lateinit var headlinesAdapter: HeadlinesAdapter
    private lateinit var rvGeneral: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var url:String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel=(activity as Home).viewModel

        rvGeneral = view.findViewById(R.id.rvGeneral)
        progressBar = view.findViewById(R.id.progress_bar)
        setUpRecyclerView()

        viewModel.getGeneralHeadlines("us")
        viewModel.generalHeadlines.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let {newsResponse->
                        headlinesAdapter.differ.submitList(newsResponse.articles)
                        val totalPages = newsResponse.totalResults / Const.QUERY_PAGE_SIZE + 2
                        isAtLastPage = viewModel.generalPageNumber == totalPages
                        if (isAtLastPage){
                            rvGeneral.setPadding(0,0,0,0)
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
            url = it.url.toString()
            startActivity(intent)
        }

    }

    private fun setUpRecyclerView(){
        headlinesAdapter = HeadlinesAdapter(this)
        rvGeneral.apply {
            adapter = headlinesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@General.scrollListener)
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
                viewModel.getGeneralHeadlines("in")
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
                val email = "kk4712621@gmail.com,kapil.saini.01999@gmail.com"
                val addresses = email.split("," ).toString()
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:$email")
                intent.putExtra(Intent.EXTRA_EMAIL,addresses)

                if (intent.resolveActivity(requireContext().packageManager)!=null){
                    startActivity(intent)
                }
            }
            R.id.full_article ->{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }

}