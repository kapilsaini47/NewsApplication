package com.kapil.android.newsapp.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kapil.android.newsapp.R
import com.kapil.android.newsapp.adapter.HeadlinesAdapter
import com.kapil.android.newsapp.domain.models.Article
import com.kapil.android.newsapp.onclickInterface.onClickInterface
import com.kapil.android.newsapp.ui.activity.ArticleActivity
import com.kapil.android.newsapp.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedArticles : Fragment(R.layout.fragment_saved_articles), onClickInterface {

    // var viewModel: NewsViewModel
    val viewModel by viewModels<NewsViewModel>()
    private lateinit var rvSavedArticles: RecyclerView
    private lateinit var headlinesAdapter: HeadlinesAdapter
    private lateinit var noArticleSaveText: TextView
    private var url:String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       //viewModel = (activity as Home).viewModel

        activity?.title = "Saved Article"
        rvSavedArticles = view.findViewById(R.id.rvSavedArticle)
        noArticleSaveText = view.findViewById(R.id.tvSavedArticle)

        setUpRecyclerview()

        viewModel.getAllArticles().observe(viewLifecycleOwner, Observer { response->
            if (response.isEmpty()){
                noArticleSaveText.visibility = View.VISIBLE
            }else{
                noArticleSaveText.visibility = View.INVISIBLE
                headlinesAdapter.differ.submitList(response)
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

        //swipe right to delete article
        val itemTouchHelper = object :ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val article = headlinesAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view, "Article Successfully deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.insertArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(rvSavedArticles)
        }

    }

    private fun setUpRecyclerview(){
        headlinesAdapter = HeadlinesAdapter(this)
        rvSavedArticles.apply {
            adapter = headlinesAdapter
            layoutManager = LinearLayoutManager(activity)
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
            R.id.full_article ->{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }

}
