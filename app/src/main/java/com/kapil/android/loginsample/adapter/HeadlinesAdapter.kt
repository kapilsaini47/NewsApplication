package com.kapil.android.loginsample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.loginsample.R
import com.kapil.android.loginsample.models.Article
import com.squareup.picasso.Picasso

class HeadlinesAdapter(): RecyclerView.Adapter<HeadlinesAdapter.HeadlinesViewHolder>() {

    inner class HeadlinesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imgHeadlines = itemView.findViewById<ImageView>(R.id.img_headlines)
        var titles = itemView.findViewById<TextView>(R.id.title_headlines)
        var source = itemView.findViewById<TextView>(R.id.author_headlines)

        fun bind(articles: Article){
            titles.text = articles.title
            source.text = articles.source?.name
            Picasso.get().load(articles.urlToImage).error(R.mipmap.broken_news_logo).into(imgHeadlines)
        }

    }

    //object for difference call back from recyclerView
    private  val diffUtilCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtilCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinesViewHolder {
        return HeadlinesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.topheadlines_single_view, parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: HeadlinesViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
        holder.itemView.apply {
            setOnClickListener{
                onItemClickListener?.let { it(article) }
            }
        }
    }

    private var onItemClickListener:((Article)-> Unit)? = null

    fun setOnItemClickListener(listener: (Article)-> Unit){
        onItemClickListener= listener
    }
}