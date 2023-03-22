package com.kapil.android.loginsample.repository

import com.kapil.android.loginsample.db.ArticleDatabase
import com.kapil.android.loginsample.models.Article

class Repository(val articleDatabase: ArticleDatabase) {

    //call suspend function from api
    suspend fun getHeadlines(countryCode: String,pageNumber:Int)=
        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    suspend fun getCategoryHeadlines(countryCode: String,pageNumber: Int,category: String)=
        RetrofitInstance.api.getCategoryHeadlines(countryCode,pageNumber,category)

    suspend fun saveArticle(article: Article) = articleDatabase.getArticleDao().upsert(article)

    fun getAllArticle() = articleDatabase.getArticleDao().getAllArticle()

    suspend fun deleteArticle(article: Article) = articleDatabase.getArticleDao().deleteArticle(article)

    suspend fun searchNews(query:String,pageNumber: Int)=
        RetrofitInstance.api.searchNews(query,pageNumber)
}