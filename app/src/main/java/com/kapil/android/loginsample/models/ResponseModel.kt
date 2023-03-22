package com.kapil.android.loginsample.models

data class ResponseModel(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)