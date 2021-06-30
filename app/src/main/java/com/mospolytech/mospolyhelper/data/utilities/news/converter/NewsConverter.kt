package com.mospolytech.mospolyhelper.data.utilities.news.converter

import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import org.jsoup.Jsoup
import java.time.DateTimeException
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class NewsConverter {
    companion object {
        private val dateFormatter = DateTimeFormatter
            .ofPattern("d MMMM")
            .withLocale(Locale("ru"))
    }

    fun parseNews(newsHtml: String): List<NewsPreview> {
        val html = Jsoup.parse(newsHtml)
        val newsCards = html.getElementsByClass("card-news-list__item")
        return newsCards.map { element ->
            val newsUrl = "https://new.mospolytech.ru" +
                    (element.getElementsByTag("a").firstOrNull()?.attr("href") ?: "")
            val imageURL = "https://new.mospolytech.ru" +
                    (element.getElementsByTag("img").firstOrNull()?.attr("data-src") ?: "")
            val date = element.getElementsByClass("card-news__label").firstOrNull()?.text() ?: ""
            val localDate = try {
                MonthDay.from(dateFormatter.parse(date.toLowerCase()))
            } catch (e: DateTimeParseException) {
                MonthDay.now()
            } catch (e: DateTimeException) {
                MonthDay.now()
            }
            val title =  element.getElementsByClass("card-news__text").firstOrNull()?.text() ?: ""
            NewsPreview(
                title,
                localDate,
                imageURL,
                newsUrl
            )
        }
    }
}