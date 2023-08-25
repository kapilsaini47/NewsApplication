package com.kapil.android.newsapp.onclickInterface

import android.view.MenuItem
import com.kapil.android.newsapp.domain.models.Article

interface onClickInterface {

    fun onItemClick(position:Int,menuItem:MenuItem,item:Article)
}