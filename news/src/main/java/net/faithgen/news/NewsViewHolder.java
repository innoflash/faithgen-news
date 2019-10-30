package net.faithgen.news;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.innoflash.iosview.lists.ListItemView3;

public class NewsViewHolder extends RecyclerView.ViewHolder {
    private ListItemView3 newsView;

    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.newsView = (ListItemView3) itemView;
    }

    public ListItemView3 getNewsView() {
        return newsView;
    }
}
