package com.kapil.android.loginsample.home

import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.kapil.android.loginsample.MainActivity
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.databinding.FragmentHomeBinding
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.kapil.android.loginsample.singleton.SingletonManager
import kotlin.random.Random

class HomeFragment : Fragment(), HomeAdapter.OnArticleClick {

    private lateinit var homeRecyclerView : RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var frameLayout: FrameLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var url: String
    private lateinit var queriesList :MutableList<String>
    private lateinit var mAuth: FirebaseAuth

    val newsLists = ArrayList<HomeViewlist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home,
            container, false)

        setHasOptionsMenu(true)
        activity?.title = "Home"

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        frameLayout = binding.frameLayout
        progressBar = binding.progressBar
        homeRecyclerView = binding.recyclerView

        layoutManager = LinearLayoutManager(activity)
        frameLayout.isVisible = true
        homeAdapter = HomeAdapter(activity as Context, this, newsLists)

        queriesList = mutableListOf("bitcoin", "Tesla", "India", "Science", "Health", "Entertainment",
        "new", "Space", "technology", "Apple", "Android", "Samsung","India")
        val querieWord = queriesList.random()

        if (user != null){
            url =
                "https://newsapi.org/v2/everything?q=${querieWord}&apiKey=1b091f4d040147b580a70e0679b0a615"
            launchRequest()
        }else{
            startActivity(Intent(activity as Context, MainActivity::class.java))
            requireActivity().finishAffinity()
        }
        return binding.root
    }

    private fun launchRequest() {
        if (NetworkManager().checkConnectivity( activity as Context)) {

            try {
                val queue = SingletonManager.getInstance(activity as Context).requestQueue

                val pageSize = "100"
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,
                    "$url&pageSize=$pageSize", null,
                    Response.Listener {
                        val article = it.getJSONArray("articles")
                        for (i in 0 until article.length()) {
                            val articleJsonObject = article.getJSONObject(i)
                            val sources = articleJsonObject.getJSONObject("source")
                            val id = sources.getString("id")
                            val name = sources.getString("name")
                            val articleObject = HomeViewlist(
                                id,
                                name,
                                articleJsonObject.getString("author"),
                                articleJsonObject.getString("title"),
                                articleJsonObject.getString("description"),
                                articleJsonObject.getString("url"),
                                articleJsonObject.getString("urlToImage"),
                                articleJsonObject.getString("publishedAt"),
                                articleJsonObject.getString("content")
                            )
                            newsLists.add(articleObject)

                            homeAdapter.submitList(newsLists)
                            homeRecyclerView.adapter = homeAdapter
                            homeRecyclerView.layoutManager = layoutManager
                        }
                        frameLayout.isVisible = false
                        progressBar.isVisible = false
                    },
                    Response.ErrorListener {
                        Toast.makeText(context, "Retry", Toast.LENGTH_LONG).show()
                        fun onVolleyError(error: VolleyError){
                            Toast.makeText(context, "${error.message}", Toast.LENGTH_SHORT).show()
                        }

                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["User-Agent"] = "Mozilla/5.0"
                        return params
                    }
                }

                SingletonManager.getInstance(activity as Context)
                    .addToRequestQueue(jsonObjectRequest)

            }catch (e:Exception){
                Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
            }

        }else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Exit") { text, lister ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setNegativeButton("Open Settings") { text, listner ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_menu, menu)

        val menuItem: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Search news..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchArticle = searchView.query.toString().trim()
                frameLayout.isVisible = true
                progressBar.isVisible = true
                newsLists.clear()
                url =
                    "https://newsapi.org/v2/everything?q=${searchArticle}&apiKey=1b091f4d040147b580a70e0679b0a615"
                launchRequest()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(article: HomeViewlist) {
        val builder = CustomTabsIntent.Builder().build()
        builder.launchUrl(activity as Context,Uri.parse(article.url))
    }

}