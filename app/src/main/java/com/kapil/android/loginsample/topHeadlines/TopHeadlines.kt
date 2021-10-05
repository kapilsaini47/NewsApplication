package com.kapil.android.loginsample.topHeadlines

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.kapil.android.loginsample.MainActivity
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.databinding.FragmentTopHeadlinesBinding
import com.kapil.android.loginsample.home.HomeViewlist
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.kapil.android.loginsample.singleton.SingletonManager

class TopHeadlines : Fragment(), TopHeadlinesAdapter.OnListenerClick {

    private lateinit var recyclerView: RecyclerView
    private lateinit var headlinesNews: ArrayList<HomeViewlist>
    private lateinit var topHeadlinesAdapter: TopHeadlinesAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var frameLayout: FrameLayout
    private lateinit var url: String
    private  lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentTopHeadlinesBinding>(inflater,
            R.layout.fragment_top_headlines, container, false)

        activity?.title ="Top Headlines"
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        progressBar = binding.progressBar
        frameLayout = binding.frameLayout

        frameLayout.isVisible = true
        progressBar.isVisible = true

        headlinesNews = ArrayList<HomeViewlist>()
        recyclerView = binding.topHeadlinesRecyclerView
        topHeadlinesAdapter = TopHeadlinesAdapter(activity as Context,this, headlinesNews)
        layoutManager = LinearLayoutManager(activity)

        if (user != null){
        binding.business.setOnClickListener {
            frameWithListClear()
            url =
                "https://newsapi.org/v2/top-headlines?category=business&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
            TopHeadlinesLaunch()
        }

        binding.health.setOnClickListener {
            frameWithListClear()
            url =
                "https://newsapi.org/v2/top-headlines?category=health&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
            TopHeadlinesLaunch()
        }
        binding.entertainment.setOnClickListener {
            frameWithListClear()
            url =
                "https://newsapi.org/v2/top-headlines?category=entertainment&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
            TopHeadlinesLaunch()
        }
        binding.sports.setOnClickListener {
            frameWithListClear()
            url =
                "https://newsapi.org/v2/top-headlines?category=sports&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
            TopHeadlinesLaunch()
        }
        binding.general.setOnClickListener {
            frameWithListClear()
            url =
                "https://newsapi.org/v2/top-headlines?category=general&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
            TopHeadlinesLaunch()
        }
        binding.science.setOnClickListener {
            frameWithListClear()
            url =
                "https://newsapi.org/v2/top-headlines?category=science&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
            TopHeadlinesLaunch()
        }
        binding.technology.setOnClickListener {
            frameWithListClear()
            url =
                "https://newsapi.org/v2/top-headlines?category=technology&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
            TopHeadlinesLaunch()
        }

        business()
        }else{
            startActivity(Intent(activity as Context, MainActivity::class.java))
        }
        return binding.root
    }

    private fun TopHeadlinesLaunch(){
        if (NetworkManager().checkConnectivity(activity as Context)){
            try {
                frameLayout.isVisible = true
                progressBar.isVisible = true

                val queue = SingletonManager.getInstance(this.requireContext()).requestQueue

                val pageSize = "100"
                val jsonObjectRequest = object : JsonObjectRequest(Method.GET,
                    "$url&pageSize=$pageSize", null,
                    Response.Listener {
                        val jsonArray = it.getJSONArray("articles")
                        for (i in 0 until jsonArray.length()){
                            val articlesObject = jsonArray.getJSONObject(i)
                            val sources = articlesObject.getJSONObject("source")
                            val id = sources.getString("id")
                            val name = sources.getString("name")
                            val headlines = HomeViewlist(
                                id,
                                name,
                                articlesObject.getString("author"),
                                articlesObject.getString("title"),
                                articlesObject.getString("description"),
                                articlesObject.getString("url"),
                                articlesObject.getString("urlToImage"),
                                articlesObject.getString("publishedAt"),
                                articlesObject.getString("content")
                            )
                            headlinesNews.add(headlines)

                            topHeadlinesAdapter.submitList(headlinesNews)
                            recyclerView.adapter = topHeadlinesAdapter
                            recyclerView.layoutManager = layoutManager
                        }
                        frameLayout.isVisible = false
                        progressBar.isVisible = false

                    }, Response.ErrorListener {
                            Toast.makeText(context, "Some error occurred" , Toast.LENGTH_SHORT).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val params : MutableMap<String, String> = HashMap()
                        params["User-Agent"]="Mozilla/5.0"
                        return params
                    }
                }
                SingletonManager.getInstance(this.requireContext()).addToRequestQueue(jsonObjectRequest)

            }
            catch(e:Exception){
                Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
            }
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Exit"){ text, lister ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setNegativeButton("Open Settings"){text, listner ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onClick(listener: HomeViewlist) {
        val customTabs = CustomTabsIntent.Builder()
        customTabs.build().launchUrl(activity as Context, Uri.parse(listener.url))
    }

    private fun business(){
        url =
            "https://newsapi.org/v2/top-headlines?category=business&country=in&apiKey=1b091f4d040147b580a70e0679b0a615"
        TopHeadlinesLaunch()
    }
    private fun frameWithListClear(){
        frameLayout.isVisible = true
        progressBar.isVisible = true
        headlinesNews.clear()
    }
}