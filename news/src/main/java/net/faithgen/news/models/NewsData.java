package net.faithgen.news.models;

import org.itishka.gsonflatten.Flatten;

import java.util.List;

public class NewsData {
    @Flatten("data")
    private List<News> news;

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}
