package net.faithgen.news;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.faithgen.news.models.News;
import net.faithgen.news.models.NewsData;
import net.faithgen.sdk.FaithGenActivity;
import net.faithgen.sdk.http.API;
import net.faithgen.sdk.http.ErrorResponse;
import net.faithgen.sdk.http.Pagination;
import net.faithgen.sdk.http.types.ServerResponse;
import net.faithgen.sdk.singletons.GSONSingleton;
import net.faithgen.sdk.utils.Dialogs;
import net.innoflash.iosview.recyclerview.RecyclerTouchListener;
import net.innoflash.iosview.recyclerview.RecyclerViewClickListener;
import net.innoflash.iosview.swipelib.SwipeRefreshLayout;

import java.util.HashMap;
import java.util.List;

import br.com.liveo.searchliveo.SearchLiveo;

public class NewsActivity extends FaithGenActivity implements RecyclerViewClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SearchLiveo searchLiveo;
    private String filterText = "";
    private HashMap<String, String> params;
    private NewsData newsData;
    private List<News> news;
    private Pagination pagination;
    private NewsAdapter newsAdapter;
    private RecyclerView newsListView;
    private SwipeRefreshLayout newsSwiper;
    private CardView statusCard;

    @Override
    public String getPageTitle() {
        return Constants.NEWS;
    }

    @Override
    public int getPageIcon() {
        return R.drawable.news;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsSwiper = findViewById(R.id.newsSwiper);
        newsListView = findViewById(R.id.newsList);
        statusCard = findViewById(R.id.statusCard);

        newsListView.setLayoutManager(new LinearLayoutManager(this));
        newsListView.addOnItemTouchListener(new RecyclerTouchListener(this, newsListView, this));

        newsSwiper.setOnRefreshListener(this);
        newsSwiper.setRefreshDrawableStyle(SwipeRefreshLayout.ARROW);
        newsSwiper.setPullPosition(Gravity.BOTTOM);

        params = new HashMap<>();

        searchLiveo = findViewById(R.id.search_liveo);
        searchLiveo.with(this)
                .showVoice()
                .hideKeyboardAfterSearch()
                .with(this, charSequence -> {
                    filterText = (String) charSequence;
                })
                .hideSearch(() -> {
                    getToolbar().setVisibility(View.VISIBLE);
                    searchLiveo.text("");
                })
                .build();

        setOnOptionsClicked(R.drawable.ic_search_black_24dp, view -> {
            searchLiveo.show();
            getToolbar().setVisibility(View.GONE);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadNews(Constants.NEWS_ROUTE);
    }

    private void loadNews(String url) {
        params.put(Constants.FILTER_TEXT, filterText);
        API.get(NewsActivity.this, url, params, pagination == null, new ServerResponse() {
            @Override
            public void onServerResponse(String serverResponse) {
                newsData = GSONSingleton.getInstance().getGson().fromJson(serverResponse, NewsData.class);
                pagination = GSONSingleton.getInstance().getGson().fromJson(serverResponse, Pagination.class);

                if (news == null || news.size() == 0) {
                    news = newsData.getNews();
                    newsAdapter = new NewsAdapter(NewsActivity.this, news);
                    newsListView.setAdapter(newsAdapter);
                    statusCard.setVisibility(View.VISIBLE);
                } else {
                    statusCard.setVisibility(View.GONE);
                    news.addAll(newsData.getNews());
                    newsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Dialogs.showOkDialog(NewsActivity.this, errorResponse.getMessage(), pagination == null);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onRefresh() {
        if (pagination == null || pagination.getLinks().getNext() == null)
            newsSwiper.setRefreshing(false);
        else loadNews(pagination.getLinks().getNext());
    }
}
