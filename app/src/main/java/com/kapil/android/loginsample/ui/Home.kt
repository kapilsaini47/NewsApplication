package com.kapil.android.loginsample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.db.ArticleDatabase
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.kapil.android.loginsample.repository.Repository
import com.kapil.android.loginsample.viewModel.NewsFactoryViewModel
import com.kapil.android.loginsample.viewModel.NewsViewModel

class Home : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    private  lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_LoginSample)
        setContentView(R.layout.activity_home)

        val repository = Repository(ArticleDatabase(this))
        val network = NetworkManager()
        val viewModelFactory = NewsFactoryViewModel(network,repository,application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewsViewModel::class.java)

        bottomNavigationView = findViewById(R.id.bottomNavigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val  navController = navHostFragment?.findNavController()
        if (navController != null) {
            bottomNavigationView.setupWithNavController(navController)
        }
    }
}