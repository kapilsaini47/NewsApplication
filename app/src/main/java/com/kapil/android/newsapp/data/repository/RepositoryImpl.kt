package com.kapil.android.newsapp.data.repository

import android.app.Application
import com.kapil.android.newsapp.data.db.ArticleDatabase
import com.kapil.android.newsapp.data.remote.NewsApis
import com.kapil.android.newsapp.domain.models.Article
import com.kapil.android.newsapp.domain.repository.RepositoryInterface
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api:NewsApis,
    val app:Application,
    private val articleDatabase: ArticleDatabase
    ) :RepositoryInterface
{
    override suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        api.getHeadlines(countryCode,pageNumber)

//    //call suspend function from api
//    suspend fun getHeadlines(countryCode: String,pageNumber:Int)=
//        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    override suspend fun getCategoryHeadlines(countryCode: String, pageNumber: Int, category: String)=
      api.getCategoryHeadlines(countryCode,pageNumber,category)

    override suspend fun saveArticle(article: Article) = articleDatabase.getArticleDao().upsert(article)

    override fun getAllArticle() = articleDatabase.getArticleDao().getAllArticle()

    override suspend fun deleteArticle(article: Article) = articleDatabase.getArticleDao().deleteArticle(article)

    override suspend fun searchNews(query:String, pageNumber: Int)=
        api.searchNews(query,pageNumber)
}