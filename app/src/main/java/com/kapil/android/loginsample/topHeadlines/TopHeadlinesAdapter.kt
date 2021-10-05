package com.kapil.android.loginsample.topHeadlines

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.loginsample.Home
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.home.HomeViewlist
import com.squareup.picasso.Picasso
import java.util.zip.Inflater

class TopHeadlinesAdapter(val context:Context, private val listener: OnListenerClick,
                          val list:ArrayList<HomeViewlist>):
    ListAdapter<HomeViewlist,TopHeadlinesAdapter.TopHeadlinesViewHolder>(TopHeadlinesDiffUtilCallback()) {

    class TopHeadlinesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var headlinesImage: ImageView = itemView.findViewById(R.id.img_headlines)
        var headlinesTitle: TextView = itemView.findViewById(R.id.title_headlines)
        var headlinesAuthor: TextView = itemView.findViewById(R.id.author_headlines)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHeadlinesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topheadlines_single_view,
            parent, false )
        val viewHolder = TopHeadlinesViewHolder(view)

        view.setOnClickListener {
            listener.onClick(list[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TopHeadlinesViewHolder, position: Int) {
        val headlines = getItem(position)
        holder.headlinesTitle.text = headlines.title
        holder.headlinesAuthor.text = headlines.name

        if(headlines.urlToImage.isEmpty()){
            holder.headlinesImage.setImageResource(R.mipmap.broken_news_logo)
        }else {
            Picasso.get().load(headlines.urlToImage).error(R.mipmap.broken_news_logo)
                .into(holder.headlinesImage)
        }
    }

    interface OnListenerClick{
        fun onClick(listener:HomeViewlist)
    }

    class TopHeadlinesDiffUtilCallback(): DiffUtil.ItemCallback<HomeViewlist>(){
        override fun areItemsTheSame(oldItem: HomeViewlist, newItem: HomeViewlist): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: HomeViewlist, newItem: HomeViewlist): Boolean {
            return oldItem == newItem
        }

    }
}