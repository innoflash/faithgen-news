package net.faithgen.news;

import android.os.Bundle;

import net.faithgen.sdk.FaithGenActivity;

public class NewsActivity extends FaithGenActivity {

    private String NEWS = "News";

    @Override
    public String getPageTitle() {
        return NEWS;
    }

    @Override
    public int getPageIcon() {
        return R.drawable.news;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
    }
}
