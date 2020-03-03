package net.faithgen.news.models

import net.faithgen.sdk.models.Avatar
import net.faithgen.sdk.models.Date
import org.itishka.gsonflatten.Flatten

/**
 * Maps article from JSON
 */
final data class Article(
    val id: String,
    val title: String,
    val news: String,
    val date : Date,
    val avatar : Avatar,
    @Flatten("comments::count")
    val commentsCount : Int
)