package com.kapil.android.newsapp.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import com.kapil.android.newsapp.R
import com.kapil.android.newsapp.domain.networkmanager.NetworkManager

class PolicyActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var webSettings: WebSettings

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_LoginSample)
        setContentView(R.layout.activity_policy)

    webView = findViewById(R.id.wvPolicy)
    progressBar = findViewById(R.id.progress_bar)

        supportActionBar?.title = "Privacy Policy"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    showProgressBar()
    val networkManager = NetworkManager()
    val url = "https://www.privacypolicies.com/live/d7aefb79-e3a4-431e-895e-ec1dfa460de9"

    if (networkManager.hasInternetConnection(this)){
        hideProgressBar()

        webSettings = webView.settings
        webSettings.javaScriptEnabled = true // Enable JavaScript

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    }else{
        hideProgressBar()
        Toast.makeText(this, "No Network", Toast.LENGTH_LONG).show()
    }

}
    private fun showProgressBar(){
    progressBar.visibility = View.VISIBLE
}

    private fun hideProgressBar(){
    progressBar.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.apply {
            clearCache(true)
            destroy()
        }
        progressBar.apply {
            clearAnimation()
            destroyDrawingCache()
        }
    }
}