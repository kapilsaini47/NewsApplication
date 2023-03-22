package com.kapil.android.loginsample.ui

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.db.ArticleDatabase
import com.kapil.android.loginsample.models.Article
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.kapil.android.loginsample.repository.Repository
import com.kapil.android.loginsample.viewModel.NewsFactoryViewModel
import com.kapil.android.loginsample.viewModel.NewsViewModel

class ArticleActivity() : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var fapActivity: FloatingActionButton
    private lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_LoginSample)
        setContentView(R.layout.activity_artcle)

        fapActivity = findViewById(R.id.faButton)
        webView = findViewById(R.id.wvActivity)
        progressBar = findViewById(R.id.pb)

        val repository = Repository(ArticleDatabase(this))
        val network = NetworkManager()
        val factory = NewsFactoryViewModel(network,repository,application)
        newsViewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title="Samachar"
        }
        showProgressBar()

        val intent = this.intent
        val bundle = intent.extras?.getBundle("article")
        val article: Article? = bundle?.getSerializable("article") as Article?

        if (network.hasInternetConnection(this@ArticleActivity)){
            webView.apply {
                hideProgressBar()
                webViewClient = WebViewClient()
                article?.url?.let { loadUrl(it) }
            }
        }else{
            Toast.makeText(this,"Network is not connected", Toast.LENGTH_LONG).show()
        }

        fapActivity.setOnClickListener {
            if (article != null) {
                newsViewModel.insertArticle(article)
            }
            Snackbar.make(it, "Article Saved Successfully", Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBar.visibility = View.GONE
    }
}