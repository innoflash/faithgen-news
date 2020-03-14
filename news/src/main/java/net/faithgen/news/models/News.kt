package net.faithgen.news.models

import com.google.gson.annotations.SerializedName

/**
 * Maps news from the server
 */
final data class News(
        @SerializedName("news")
        val articles: MutableList<Article>
)