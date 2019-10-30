package net.faithgen.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.faithgen.news.models.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private Context context;
    private List<News> news;
    private LayoutInflater layoutInflater;
    private News article;

    public NewsAdapter(Context context, List<News> news) {
        this.context = context;
        this.news = news;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(layoutInflater.inflate(R.layout.list_news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        article = news.get(position);
        holder.getNewsView().setItemHeading(article.getDate().getApprox());
        holder.getNewsView().setItemContent(article.getTitle());
        holder.getNewsView().setItemFooter(article.getCommentsCount() + " comments");
        Picasso.get()
                .load(article.getAvatar().get_50())
                .placeholder(R.drawable.news)
                .error(R.drawable.news)
                .into(holder.getNewsView().getImageView());
    }

    @Override
    public int getItemCount() {
        if (news == null) return 0;
        return news.size();
    }
}
