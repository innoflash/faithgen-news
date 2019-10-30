package net.faithgen.news;

import android.os.Bundle;
import android.view.View;

import net.faithgen.sdk.FaithGenActivity;
import net.faithgen.sdk.http.API;
import net.faithgen.sdk.http.ErrorResponse;
import net.faithgen.sdk.http.types.ServerResponse;
import net.faithgen.sdk.utils.Dialogs;

import java.util.HashMap;

import br.com.liveo.searchliveo.SearchLiveo;

public class NewsActivity extends FaithGenActivity {

    private SearchLiveo searchLiveo;
    private String filterText = "";
    private HashMap<String, String> params;

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
        API.get(NewsActivity.this, url, params, false, new ServerResponse() {
            @Override
            public void onServerResponse(String serverResponse) {

            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Dialogs.showOkDialog(NewsActivity.this, errorResponse.getMessage(), false);
            }
        });
    }
}
