package net.faithgen.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.faithgen.articles.R;
import net.faithgen.news.models.Article;
import net.faithgen.news.models.News;
import net.faithgen.sdk.FaithGenActivity;
import net.faithgen.sdk.http.ErrorResponse;
import net.faithgen.sdk.http.FaithGenAPI;
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
    private News news;
    private List<Article> articles;
    private Pagination pagination;
    private NewsAdapter newsAdapter;
    private RecyclerView newsListView;
    private SwipeRefreshLayout newsSwiper;
    private CardView statusCard;
    private Intent intent;
    private FaithGenAPI faithGenAPI;

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

        faithGenAPI = new FaithGenAPI(this);

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
        searchLiveo.with(this, charSequence -> {
            filterText = (String) charSequence;
            loadNews(Constants.NEWS_ROUTE, true);
        })
                .showVoice()
                .hideKeyboardAfterSearch()
                .hideSearch(() -> {
                    getToolbar().setVisibility(View.VISIBLE);
                    if (!filterText.isEmpty()) {
                        filterText = "";
                        searchLiveo.text(filterText);
                        loadNews(Constants.NEWS_ROUTE, true);
                    }
                })
                .build();

        setOnOptionsClicked(R.drawable.ic_search_black_24dp, view -> {
            searchLiveo.setVisibility(View.VISIBLE);
            searchLiveo.show();
            getToolbar().setVisibility(View.GONE);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (news == null)
            loadNews(Constants.NEWS_ROUTE, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        faithGenAPI.cancelRequests();
    }

    private ServerResponse getServerResponse(boolean reload) {
        return new ServerResponse() {
            @Override
            public void onServerResponse(String serverResponse) {
                news = GSONSingleton.Companion.getInstance().getGson().fromJson(serverResponse, News.class);
                pagination = GSONSingleton.Companion.getInstance().getGson().fromJson(serverResponse, Pagination.class);

                if (reload || articles == null || articles.size() == 0) {
                    articles = news.getArticles();
                    newsAdapter = new NewsAdapter(NewsActivity.this, articles);
                    newsListView.setAdapter(newsAdapter);
                } else {
                    articles.addAll(news.getArticles());
                    newsAdapter.notifyDataSetChanged();
                }

                if (articles.size() == 0) statusCard.setVisibility(View.VISIBLE);
                else statusCard.setVisibility(View.GONE);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Dialogs.showOkDialog(NewsActivity.this, errorResponse.getMessage(), pagination == null);
            }
        };
    }

    private void loadNews(String url, boolean reload) {
        params.put(Constants.FILTER_TEXT, filterText);
        faithGenAPI
                .setParams(params)
                .setProcess(Constants.FETCHING_ARTICLES)
                .setServerResponse(getServerResponse(reload))
                .request(url);
    }

    @Override
    public void onClick(View view, int position) {
        intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(Constants.ID, articles.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onRefresh() {
        if (pagination == null || pagination.getLinks().getNext() == null)
            newsSwiper.setRefreshing(false);
        else loadNews(pagination.getLinks().getNext(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                searchLiveo.resultVoice(requestCode, resultCode, data);
            }
        }
    }
}
