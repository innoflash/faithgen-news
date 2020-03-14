package net.faithgen.news

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article.*
import net.faithgen.articles.R
import net.faithgen.news.models.Article
import net.faithgen.sdk.FaithGenActivity
import net.faithgen.sdk.SDK
import net.faithgen.sdk.comments.CommentsSettings
import net.faithgen.sdk.http.ErrorResponse
import net.faithgen.sdk.http.FaithGenAPI
import net.faithgen.sdk.http.types.ServerResponse
import net.faithgen.sdk.menu.MenuFactory
import net.faithgen.sdk.menu.MenuItem
import net.faithgen.sdk.menu.MenuListener
import net.faithgen.sdk.singletons.GSONSingleton
import net.faithgen.sdk.utils.Dialogs
import net.faithgen.sdk.utils.Utils.shareText
import net.innoflash.iosview.Toolbar.OptionsClicked

class ArticleActivity : FaithGenActivity() {
    private var article: Article? = null

    private val faithGenAPI: FaithGenAPI by lazy { FaithGenAPI(this) }

    private val articleId: String by lazy { intent.getStringExtra(Constants.ID) }

    private val menuItems: List<MenuItem> = listOf(
            MenuItem(R.drawable.ic_share_black_24dp, Constants.SHARE),
            MenuItem(R.drawable.ic_chat_black_24dp, Constants.COMMENT)
    )

    override fun getPageTitle(): String? {
        return Constants.ARTICLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        initMenu()
    }

    private fun initMenu() {
        val menu = MenuFactory.initializeMenu(this, menuItems)
        menu.setOnMenuItemListener(Constants.ARTICLE_OPTIONS, MenuListener { _: MenuItem?, position: Int ->
            when (position) {
                0 -> {
                    val message: String = article!!.title + "\nBy : " +
                            SDK.getMinistry().name + "\n\nDate : " +
                            Html.escapeHtml(article!!.news) + "\n\n" +
                            "Link : http://articlelink \n"
                    shareText(this@ArticleActivity, message, "Article")
                }
                1 -> SDK.openComments(this@ArticleActivity, CommentsSettings.Builder()
                        .setTitle(article!!.title)
                        .setItemId(articleId)
                        .setCategory("news/")
                        .build())
            }
        })
        onOptionsClicked = OptionsClicked { menu.show() }
    }

    override fun onStart() {
        super.onStart()
        faithGenAPI
                .setProcess(Constants.OPENING_ARTICLE)
                .setServerResponse(getServerResponse())
                .request(Constants.NEWS_ROUTE + "/" + articleId)
    }

    private fun getServerResponse(): ServerResponse? {
        return object : ServerResponse() {
            override fun onServerResponse(serverResponse: String) {
                article = GSONSingleton.instance.gson.fromJson(serverResponse, Article::class.java)
                renderArticle()
            }

            override fun onError(errorResponse: ErrorResponse?) {
                Dialogs.showOkDialog(this@ArticleActivity, errorResponse!!.message, true)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        faithGenAPI.cancelRequests()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private fun renderArticle() {
        if (article != null) {
            Picasso.get()
                    .load(article!!.avatar.original)
                    .placeholder(R.drawable.news_512)
                    .error(R.drawable.news_512)
                    .into(banner)

            summary.text = article!!.commentsCount.toString() + " comments / " + article!!.date.approx
            articleTitle.text = article!!.title
            date.text = article!!.date.formatted
            news.text = Html.fromHtml(article!!.news, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}