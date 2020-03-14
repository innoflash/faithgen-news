package net.faithgen.news

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.liveo.searchliveo.SearchLiveo
import kotlinx.android.synthetic.main.activity_news.*
import net.faithgen.articles.R
import net.faithgen.news.models.Article
import net.faithgen.news.models.News
import net.faithgen.sdk.FaithGenActivity
import net.faithgen.sdk.http.ErrorResponse
import net.faithgen.sdk.http.FaithGenAPI
import net.faithgen.sdk.http.Pagination
import net.faithgen.sdk.http.types.ServerResponse
import net.faithgen.sdk.singletons.GSONSingleton
import net.faithgen.sdk.utils.Dialogs
import net.innoflash.iosview.recyclerview.RecyclerTouchListener
import net.innoflash.iosview.recyclerview.RecyclerViewClickListener
import net.innoflash.iosview.swipelib.SwipeRefreshLayout

class NewsActivity : FaithGenActivity(), RecyclerViewClickListener, SwipeRefreshLayout.OnRefreshListener {
    private var filterText = ""
    private var news: News? = null
    private var pagination: Pagination? = null
    private var newsAdapter: NewsAdapter? = null
    private var articles: MutableList<Article>? = null

    private val faithGenAPI: FaithGenAPI by lazy { FaithGenAPI(this) }

    private val params: MutableMap<String, String> = mutableMapOf()

    override fun getPageTitle(): String? {
        return Constants.NEWS
    }

    override fun getPageIcon(): Int {
        return R.drawable.news
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        newsList.layoutManager = LinearLayoutManager(this)
        newsList.addOnItemTouchListener(RecyclerTouchListener(this, newsList, this))

        newsSwiper.setOnRefreshListener(this)
        newsSwiper.setRefreshDrawableStyle(SwipeRefreshLayout.ARROW)
        newsSwiper.setPullPosition(Gravity.BOTTOM)

        search_liveo.with(this) { charSequence ->
            filterText = charSequence as String
            loadNews(Constants.NEWS_ROUTE, true)
        }
                .showVoice()
                .hideKeyboardAfterSearch()
                .hideSearch {
                    toolbar.visibility = View.VISIBLE
                    if (filterText.isNotEmpty()) {
                        filterText = ""
                        search_liveo.text(filterText)
                        loadNews(Constants.NEWS_ROUTE, true)
                    }
                }.build()

        setOnOptionsClicked(R.drawable.ic_search_black_24dp) {
            search_liveo.visibility = View.VISIBLE
            search_liveo.show()
            toolbar.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        if (news == null) loadNews(Constants.NEWS_ROUTE, true)
    }

    override fun onStop() {
        super.onStop()
        faithGenAPI.cancelRequests()
    }

    private fun getServerResponse(reload: Boolean): ServerResponse? {
        return object : ServerResponse() {
            override fun onServerResponse(serverResponse: String) {
                news = GSONSingleton.instance.gson.fromJson(serverResponse, News::class.java)
                pagination = GSONSingleton.instance.gson.fromJson(serverResponse, Pagination::class.java)
                if (reload || articles == null || articles!!.isEmpty()) {
                    articles = news!!.articles
                    newsAdapter = NewsAdapter(this@NewsActivity, articles)
                    newsList.adapter = newsAdapter
                } else {
                    articles!!.addAll(news!!.articles)
                    newsAdapter!!.notifyDataSetChanged()
                }
                if (articles!!.isEmpty()) statusCard.visibility = View.VISIBLE else statusCard.visibility = View.GONE
            }

            override fun onError(errorResponse: ErrorResponse?) {
                Dialogs.showOkDialog(this@NewsActivity, errorResponse!!.message, pagination === null)
            }
        }
    }

    private fun loadNews(url: String, reload: Boolean) {
        params[Constants.FILTER_TEXT] = filterText
        faithGenAPI
                .setParams(params as HashMap<String, String>)
                .setProcess(Constants.FETCHING_ARTICLES)
                .setServerResponse(getServerResponse(reload))
                .request(url)
    }

    override fun onClick(view: View?, position: Int) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra(Constants.ID, articles!![position].id)
        startActivity(intent)
    }

    override fun onLongClick(view: View?, position: Int) {
    }

    override fun onRefresh() {
        if (pagination == null || pagination!!.links.next.isNullOrBlank())
            newsSwiper.isRefreshing = false
        else loadNews(pagination!!.links.next, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                search_liveo.resultVoice(requestCode, resultCode, data)
            }
        }
    }
}