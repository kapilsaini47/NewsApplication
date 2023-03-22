package com.kapil.android.loginsample.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kapil.android.loginsample.NewsApplication
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.kapil.android.loginsample.repository.Repository

class NewsFactoryViewModel(
    val networkManager: NetworkManager,
    val repository: Repository,
    val app: Application
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(repository,networkManager,app) as T
    }
}