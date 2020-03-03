package net.faithgen.news

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.faithgen.articles.R
import net.faithgen.news.models.Article
import net.innoflash.iosview.lists.ListItemView3

final class NewsAdapter(private val context: Context, private val articles: List<Article>?) : RecyclerView.Adapter<NewsViewHolder>() {
    private val layoutInflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val newsView = layoutInflater.inflate(R.layout.list_news_item, parent, false) as ListItemView3
        return NewsViewHolder(newsView)
    }

    override fun getItemCount(): Int {
        if (articles == null) return 0
        else return articles.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles?.get(position)!!
        holder.newsView.itemHeading = article.date.approx
        holder.newsView.itemContent = article.title
        holder.newsView.itemFooter = article.commentsCount.toString() + " comments"
        Picasso.get()
                .load(article.avatar._100)
                .placeholder(R.drawable.news)
                .error(R.drawable.news)
                .into(holder.newsView.imageView)
    }
}