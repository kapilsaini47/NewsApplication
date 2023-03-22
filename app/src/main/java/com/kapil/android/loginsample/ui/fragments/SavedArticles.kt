package com.kapil.android.loginsample.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.adapter.HeadlinesAdapter
import com.kapil.android.loginsample.ui.ArticleActivity
import com.kapil.android.loginsample.ui.Home
import com.kapil.android.loginsample.viewModel.NewsViewModel

class SavedArticles : Fragment(R.layout.fragment_saved_articles){

    lateinit var viewModel: NewsViewModel
    private lateinit var rvSavedArticles: RecyclerView
    private lateinit var headlinesAdapter: HeadlinesAdapter
    private lateinit var noArticleSaveText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as Home).viewModel

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
        headlinesAdapter = HeadlinesAdapter()
        rvSavedArticles.apply {
            adapter = headlinesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}
