package com.kapil.android.newsapp.data.remote

import com.kapil.android.newsapp.domain.models.ResponseModel
import com.kapil.android.newsapp.util.Const
import com.kapil.android.newsapp.util.Const.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApis {

    //call using coroutines
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        countryCode:String= "in",
        @Query("page")
        pageInteger: Int=1,
        @Query("apiKey")
        apiKey:String= Const.API_KEY
    ): Response<ResponseModel>

    @GET("v2/top-headlines")
    suspend fun getCategoryHeadlines(
        @Query("country")
        countryCode: String="in",
        @Query("page")
        pageNumber: Int=1,
        @Query("category")
        category: String="business",
        @Query("apiKey")
        apiKey: String=Const.API_KEY
    ):Response<ResponseModel>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber:Int=1,
        @Query("apiKey")
        apiKey: String=API_KEY
    ):Response<ResponseModel>
}