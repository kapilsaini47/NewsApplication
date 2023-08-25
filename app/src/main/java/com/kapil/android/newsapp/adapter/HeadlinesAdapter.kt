package com.kapil.android.newsapp.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kapil.android.newsapp.R
import com.kapil.android.newsapp.domain.models.Article
import com.kapil.android.newsapp.onclickInterface.onClickInterface
import com.squareup.picasso.Picasso

class HeadlinesAdapter(
    private val onClickListener:onClickInterface
): RecyclerView.Adapter<HeadlinesAdapter.HeadlinesViewHolder>() {

    inner class HeadlinesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        var imgHeadlines = itemView.findViewById<ImageView>(R.id.img_headlines)
        var titles = itemView.findViewById<TextView>(R.id.title_headlines)
        var source = itemView.findViewById<TextView>(R.id.author_headlines)
        var optionMenu = itemView.findViewById<ImageView>(R.id.imgOption)

        fun bind(articles: Article){
            titles.text = articles.title
            source.text = articles.source?.name
            Picasso.get().load(articles.urlToImage).error(R.mipmap.broken_news_logo).into(imgHeadlines)
            optionMenu.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val popupMenu = PopupMenu(v?.context,v)
            popupMenu.menuInflater.inflate(R.menu.headline_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(menu: MenuItem?): Boolean {
            val position = adapterPosition
            if (menu!=null){
                onClickListener.onItemClick(position,menu,differ.currentList[position])
            }
            return false
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