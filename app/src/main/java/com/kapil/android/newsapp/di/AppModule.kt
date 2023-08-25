package com.kapil.android.newsapp.di

import android.app.Application
import androidx.room.Room
import com.kapil.android.newsapp.data.db.ArticleDatabase
import com.kapil.android.newsapp.data.remote.NewsApis
import com.kapil.android.newsapp.domain.networkmanager.NetworkManager
import com.kapil.android.newsapp.util.Const.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance():NewsApis{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApis::class.java)
    }

    @Provides
    @Singleton
    fun provideDBInstance(app:Application):ArticleDatabase{
        return Room.databaseBuilder(app,ArticleDatabase::class.java,"article-database")
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkManagerInstance():NetworkManager {
        return NetworkManager()
    }
}