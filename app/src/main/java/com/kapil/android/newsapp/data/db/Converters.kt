package com.kapil.android.newsapp.data.db

import androidx.room.TypeConverter
import com.kapil.android.newsapp.domain.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String? {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name,name)
    }
}