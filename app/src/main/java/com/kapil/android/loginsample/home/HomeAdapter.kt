package com.kapil.android.loginsample.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.topHeadlines.TopHeadlines
import com.squareup.picasso.Picasso

class HomeAdapter(
    var context: Context, private val listner: HomeFragment,
    var newsList: ArrayList<HomeViewlist>) :
   ListAdapter<HomeViewlist,HomeAdapter.HomeViewHolder>(HomeDiffUtilCallback()){

    class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var author : TextView = itemView.findViewById(R.id.author)
        var newsImage: ImageView = itemView.findViewById(R.id.img_news)
        var newsHeading: TextView = itemView.findViewById(R.id.title)

       fun bind(news: HomeViewlist) {
          author.text = news.name
            newsHeading.text = news.title
           Picasso.get().load(news.urlToImage).error(R.mipmap.broken_news_logo).into(newsImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_home_view, parent,
            false)
        val viewHolder = HomeViewHolder(view)
        view.setOnClickListener {
            listner.onClick(newsList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    //chrome tab interface for click listener
    interface OnArticleClick{
        fun onClick(article: HomeViewlist)
    }

    class HomeDiffUtilCallback : DiffUtil.ItemCallback<HomeViewlist>(){
        override fun areItemsTheSame(oldItem: HomeViewlist, newItem: HomeViewlist): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: HomeViewlist, newItem: HomeViewlist): Boolean {
           return oldItem == newItem
        }
    }

}