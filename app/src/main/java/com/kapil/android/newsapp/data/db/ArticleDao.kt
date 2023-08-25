package com.kapil.android.newsapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kapil.android.newsapp.domain.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long

    @Query("SELECT * FROM articleTable")
    fun getAllArticle(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}