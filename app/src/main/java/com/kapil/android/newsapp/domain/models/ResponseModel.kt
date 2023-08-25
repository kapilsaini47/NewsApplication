package com.kapil.android.newsapp.domain.models

data class ResponseModel(
    var articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)