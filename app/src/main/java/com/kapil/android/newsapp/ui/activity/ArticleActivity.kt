package com.kapil.android.newsapp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kapil.android.newsapp.R
import com.kapil.android.newsapp.domain.models.Article
import com.kapil.android.newsapp.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference


@AndroidEntryPoint
class ArticleActivity () : AppCompatActivity() {

    private var webViewRef: WeakReference<WebView>? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var fapActivity: FloatingActionButton
    val viewModel by viewModels<NewsViewModel>()
    private var article: Article? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_LoginSample)
        setContentView(R.layout.activity_artcle)

        fapActivity = findViewById(R.id.faButton)
        webViewRef = WeakReference(findViewById(R.id.wvActivity))
        progressBar = findViewById(R.id.pb)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title="Samachar"
        }
        showProgressBar()

        val intent = this.intent
        val bundle = intent.extras?.getBundle("article")
        article = bundle?.getSerializable("article") as Article?

            val webView = webViewRef?.get()
            webView?.apply {
            hideProgressBar()
            webViewClient = WebViewClient()
            article?.url?.let { loadUrl(it) }
        }

        fapActivity.setOnClickListener {
            if (article != null) {
                viewModel.insertArticle(article!!)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.article_option_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share ->{
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, article?.url.toString())
                startActivity(Intent.createChooser(intent, "Share via"))
            }
            R.id.report ->{
                val email = "kapil.saini.01999@gmail.com"
                val addresses = email.split("," ).toString()
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:$email")
                intent.putExtra(Intent.EXTRA_EMAIL,addresses)

                if (intent.resolveActivity(this.packageManager)!=null){
                    startActivity(intent)
                }
            }
            R.id.full_article ->{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article?.url.toString()))
                startActivity(intent)
            }
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        webViewRef?.get()?.apply {
            clearCache(true)
            destroy()
        }
    }
}