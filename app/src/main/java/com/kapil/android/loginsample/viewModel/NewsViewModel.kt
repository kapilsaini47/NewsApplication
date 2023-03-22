package com.kapil.android.loginsample.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kapil.android.loginsample.NewsApplication
import com.kapil.android.loginsample.models.Article
import com.kapil.android.loginsample.models.ResponseModel
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.kapil.android.loginsample.repository.Repository
import com.kapil.android.loginsample.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    val repo: Repository,
    private val networkManager: NetworkManager,
    app:Application
): AndroidViewModel(app){

    val headlines: MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    var headlinesPageNumber = 1
    private var headlinesResponse:ResponseModel? = null

    val categoryHeadlines: MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    val category = "business"
    var categoryHeadlinesPageNumber = 1
    var categoryHeadlinesResponse : ResponseModel? = null

    val entertainmentHeadlines : MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    val category1 = "entertainment"
    var entertainmentPageNumber = 1
    var entertainmentResponse:ResponseModel? = null

    val sportsHeadlines : MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    private val category2 = "sports"
    var sportsPageNumber = 1
    var sportsResponse:ResponseModel? = null

    val healthHeadlines : MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    private val category3 = "health"
    var healthPageNumber = 1
    var healthResponse:ResponseModel? = null

    val generalHeadlines : MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    private val category4 = "general"
    var generalPageNumber = 1
    private var generalResponse: ResponseModel? = null

    val technologyHeadlines : MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    private val category5 = "technology"
    var technologyPageNumber = 1
    private var technologyResponse: ResponseModel? = null

    val scienceHeadlines : MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    val category6 = "science"
    var sciencePageNumber = 1
    var scienceResponse: ResponseModel? = null

    val searchResponse: MutableLiveData<Resource<ResponseModel>> = MutableLiveData()
    var searchPageNumber = 1
    var searchResponses:ResponseModel? = null

    init {
        getHeadlines("in")
        getEntertainmentHeadlines("in")
        getBusinessHeadlines("in")
        getSportstHeadlines("in")
        getScienceHeadlines("in")
        getHealthHeadlines("in")
        getGeneralHeadlines("in")
        getTechnologyHeadlines("in")
    }
    //launching coroutine for api call
    fun getHeadlines(country:String) = viewModelScope.launch {
            handleNetworkSafeHeadlinesResponse(country)
    }

    fun getBusinessHeadlines(country: String) = viewModelScope.launch {
        handleNetworkSafeCategoryHeadlinesResponse(country)
    }
    fun getEntertainmentHeadlines(country: String) = viewModelScope.launch {
        handleNetworkSafeEntertainmentResponse(country)
    }
    fun getSportstHeadlines(country: String) = viewModelScope.launch {
        handleNetworkSafeSportsResponse(country)
    }
    fun getHealthHeadlines(country: String) = viewModelScope.launch {
        handleNetworkSafeHealthResponse(country)
    }
    fun getGeneralHeadlines(country: String) = viewModelScope.launch {
        handleNetworkSafeGeneralResponse(country)
    }
    fun getTechnologyHeadlines(country: String) = viewModelScope.launch {
        handleNetworkSafeTechnologyResponse(country)
    }
    fun getScienceHeadlines(country: String) = viewModelScope.launch {
        handleNetworkSafeScienceResponse(country)
    }

    fun getAllArticles() = repo.getAllArticle()

    fun insertArticle(article: Article) = viewModelScope.launch {
        repo.saveArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repo.deleteArticle(article)
    }

    fun searchNews(query: String) = viewModelScope.launch {
        handleNetworkSafeSearchResponse(query)
    }

    private fun handleHeadlinesResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                headlinesPageNumber++
                if (headlinesResponse == null){
                    headlinesResponse = resultResponse
                }else{
                    val oldArticles = headlinesResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(headlinesResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeHeadlinesResponse(country: String){
        headlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getHeadlines(country, headlinesPageNumber)
                headlines.postValue(handleHeadlinesResponse(response))
            } else {
                headlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Network Failure"))
                else -> headlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

    private fun handleCategoryHeadlinesResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                headlinesPageNumber++
                if (categoryHeadlinesResponse == null){
                    categoryHeadlinesResponse = resultResponse
                }else{
                    val oldArticles = categoryHeadlinesResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(categoryHeadlinesResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeCategoryHeadlinesResponse(country: String){
        categoryHeadlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getCategoryHeadlines(country,headlinesPageNumber,category)
                categoryHeadlines.postValue(handleCategoryHeadlinesResponse(response))
            } else {
                categoryHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> categoryHeadlines.postValue(Resource.Error("Network Failure"))
                else -> categoryHeadlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

    private fun handleEntertainmentResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                entertainmentPageNumber++
                if (entertainmentResponse == null){
                    entertainmentResponse = resultResponse
                }else{
                    val oldArticles = entertainmentResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(entertainmentResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeEntertainmentResponse(country: String){
        entertainmentHeadlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getCategoryHeadlines(country,headlinesPageNumber,category1)
                entertainmentHeadlines.postValue(handleEntertainmentResponse(response))
            } else {
                entertainmentHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> entertainmentHeadlines.postValue(Resource.Error("Network Failure"))
                else -> entertainmentHeadlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }
    private fun handleSportsResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                sportsPageNumber++
                if (sportsResponse == null){
                    sportsResponse = resultResponse
                }else{
                    val oldArticles = sportsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(sportsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeSportsResponse(country: String){
        sportsHeadlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getCategoryHeadlines(country,headlinesPageNumber,category2)
                sportsHeadlines.postValue(handleSportsResponse(response))
            } else {
                sportsHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> sportsHeadlines.postValue(Resource.Error("Network Failure"))
                else -> sportsHeadlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

    private fun handleHealthResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                healthPageNumber++
                if (headlinesResponse == null){
                    healthResponse = resultResponse
                }else{
                    val oldArticles = healthResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(healthResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeHealthResponse(country: String){
        healthHeadlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getCategoryHeadlines(country,headlinesPageNumber,category3)
                healthHeadlines.postValue(handleHealthResponse(response))
            } else {
                healthHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> healthHeadlines.postValue(Resource.Error("Network Failure"))
                else -> healthHeadlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

    private fun handleGeneralResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                generalPageNumber++
                if (generalResponse == null){
                    generalResponse = resultResponse
                }else{
                    val oldArticles = generalResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(generalResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeGeneralResponse(country: String){
        generalHeadlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getCategoryHeadlines(country,headlinesPageNumber,category4)
                generalHeadlines.postValue(handleGeneralResponse(response))
            } else {
                generalHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> generalHeadlines.postValue(Resource.Error("Network Failure"))
                else -> generalHeadlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

    private fun handleTechnologyResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                technologyPageNumber++
                if (technologyResponse == null){
                    technologyResponse = resultResponse
                }else{
                    val oldArticles = technologyResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(technologyResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeTechnologyResponse(country: String){
        technologyHeadlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getCategoryHeadlines(country,headlinesPageNumber,category5)
                technologyHeadlines.postValue(handleTechnologyResponse(response))
            } else {
                technologyHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> technologyHeadlines.postValue(Resource.Error("Network Failure"))
                else -> technologyHeadlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

    private fun handleScienceResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                sciencePageNumber++
                if (scienceResponse == null){
                    scienceResponse = resultResponse
                }else{
                    val oldArticles = scienceResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(scienceResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeScienceResponse(country: String){
        scienceHeadlines.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.getCategoryHeadlines(country,headlinesPageNumber,category6)
                scienceHeadlines.postValue(handleScienceResponse(response))
            } else {
                scienceHeadlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> scienceHeadlines.postValue(Resource.Error("Network Failure"))
                else -> scienceHeadlines.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

    private fun handleSearchResponse(response: Response<ResponseModel>):Resource<ResponseModel> {
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                searchPageNumber++
                if (searchResponses == null){
                    searchResponses = resultResponse
                }else{
                    val oldArticles = searchResponses?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(searchResponses?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleNetworkSafeSearchResponse(query: String){
        searchResponse.postValue(Resource.Loading())
        try {
            if (networkManager.hasInternetConnection(getApplication<NewsApplication>().applicationContext)) {
                val response = repo.searchNews(query,searchPageNumber)
                searchResponse.postValue(handleSearchResponse(response))
            } else {
                searchResponse.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> searchResponse.postValue(Resource.Error("Network Failure"))
                else -> searchResponse.postValue(Resource.Error(t.message.toString()))
            }
        }
    }

}