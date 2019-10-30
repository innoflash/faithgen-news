package net.faithgen.news.models;

import net.faithgen.sdk.models.Avatar;
import net.faithgen.sdk.models.Date;

import org.itishka.gsonflatten.Flatten;

public class News {
    private String id;
    private String title;
    private Date date;
    private Avatar avatar;
    @Flatten("comments::count")
    private int commentsCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
