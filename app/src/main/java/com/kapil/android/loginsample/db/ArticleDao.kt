package com.kapil.android.loginsample.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kapil.android.loginsample.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long

    @Query("SELECT * FROM articleTable")
    fun getAllArticle(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}