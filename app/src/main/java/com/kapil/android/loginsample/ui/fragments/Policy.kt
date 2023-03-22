package com.kapil.android.loginsample.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.kapil.android.loginsample.ui.Home
import com.kapil.android.loginsample.viewModel.NewsViewModel

class Policy : Fragment(R.layout.fragment_policy){

    lateinit var viewModel: NewsViewModel
    private lateinit var webView: WebView
    private lateinit var progressBar:ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as Home).viewModel
        webView = view.findViewById(R.id.wvPolicy)
        progressBar = view.findViewById(R.id.progress_bar)

        showProgressBar()
        val networkManager = NetworkManager()
        val url = "www.freeprivacypolicy.com/live/0a12fc02-2673-4936-bad7-fba2685c406f"

        if (context?.let { networkManager.hasInternetConnection(it) } == true){
            hideProgressBar()
            webView.apply {
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        }else{
            hideProgressBar()
            Toast.makeText(context, "No Network", Toast.LENGTH_LONG).show()
        }

    }
    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBar.visibility = View.INVISIBLE
    }

}