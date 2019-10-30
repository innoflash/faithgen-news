package net.faithgen.news.models;

import org.itishka.gsonflatten.Flatten;

import java.util.List;

public class News {
    @Flatten("data")
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
