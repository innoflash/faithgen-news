package net.faithgen.news;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.faithgen.articles.R;
import net.faithgen.news.models.Article;
import net.faithgen.sdk.FaithGenActivity;
import net.faithgen.sdk.SDK;
import net.faithgen.sdk.comments.CommentsSettings;
import net.faithgen.sdk.http.ErrorResponse;
import net.faithgen.sdk.http.FaithGenAPI;
import net.faithgen.sdk.http.types.ServerResponse;
import net.faithgen.sdk.menu.Menu;
import net.faithgen.sdk.menu.MenuFactory;
import net.faithgen.sdk.menu.MenuItem;
import net.faithgen.sdk.singletons.GSONSingleton;
import net.faithgen.sdk.utils.Dialogs;
import net.faithgen.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends FaithGenActivity {

    private String articleId;
    private Article article;
    private ImageView banner;
    private TextView summary;
    private TextView title;
    private TextView date;
    private TextView news;
    private Menu menu;
    private List<MenuItem> menuItems;
    private FaithGenAPI faithGenAPI;

    @Override
    public String getPageTitle() {
        return Constants.ARTICLE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        faithGenAPI = new FaithGenAPI(this);
        articleId = getIntent().getStringExtra(Constants.ID);

        banner = findViewById(R.id.banner);
        summary = findViewById(R.id.summary);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        news = findViewById(R.id.news);

        initMenu();
    }


    private void initMenu() {
        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.drawable.ic_share_black_24dp, Constants.SHARE));
        menuItems.add(new MenuItem(R.drawable.ic_chat_black_24dp, Constants.COMMENT));

        menu = MenuFactory.initializeMenu(this, menuItems);
        menu.setOnMenuItemListener(Constants.ARTICLE_OPTIONS, (menuItem, position) -> {
            switch (position) {
                case 0:
                    String message = article.getTitle() + "\nBy : " +
                            SDK.getMinistry().getName() + "\n\nDate : " +
                            Html.escapeHtml(article.getNews()) + "\n\n" +
                            "Link : http://articlelink \n";
                    Utils.INSTANCE.shareText(ArticleActivity.this, message, "Article");
                    break;
                case 1:
                    SDK.openComments(ArticleActivity.this, new CommentsSettings.Builder()
                            .setTitle(article.getTitle())
                            .setItemId(articleId)
                            .setCategory("news/")
                            .build());
                    break;
            }
        });
        setOnOptionsClicked(view -> menu.show());
    }


    @Override
    protected void onStart() {
        super.onStart();
        faithGenAPI
                .setProcess(Constants.OPENING_ARTICLE)
                .request(Constants.NEWS_ROUTE + "/" + articleId);
    }

    private ServerResponse getServerResponse() {
        return new ServerResponse() {
            @Override
            public void onServerResponse(String serverResponse) {
                article = GSONSingleton.Companion.getInstance().getGson().fromJson(serverResponse, Article.class);
                renderArticle();
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                Dialogs.showOkDialog(ArticleActivity.this, errorResponse.getMessage(), true);
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        faithGenAPI.cancelRequests();
    }

    private void renderArticle() {
        if (article != null) {
            Picasso.get()
                    .load(article.getAvatar().getOriginal())
                    .placeholder(R.drawable.news_512)
                    .error(R.drawable.news_512)
                    .into(banner);

            summary.setText(article.getCommentsCount() + " comments / " + article.getDate().getApprox());
            title.setText(article.getTitle());
            date.setText(article.getDate().getFormatted());
            news.setText(Html.fromHtml(article.getNews()));
        }
    }

}
