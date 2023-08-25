package com.kapil.android.newsapp.domain.repository

import androidx.lifecycle.LiveData
import com.kapil.android.newsapp.domain.models.Article
import com.kapil.android.newsapp.domain.models.ResponseModel
import retrofit2.Response

interface RepositoryInterface {

    suspend fun getHeadlines(countryCode:String,pageNumber:Int): Response<ResponseModel>
    suspend fun getCategoryHeadlines(countryCode: String,pageNumber: Int,category: String):Response<ResponseModel>
    suspend fun saveArticle(article: Article):Long
    fun getAllArticle():LiveData<List<Article>>
    suspend fun deleteArticle(article: Article)
    suspend fun searchNews(query: String,pageNumber: Int):Response<ResponseModel>


}