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

    fun parseNewsYear(newsHtml: String): Int {
        return Jsoup.parse(newsHtml)
            .getElementsByClass("news-detail-head__date").first()
            .getElementsByClass("numerical-item__label").first()
            .text().trim().split(' ').last().toInt()
    }

    fun parseFirstNewsUrl(newsHtml: String): String? {
        val html = Jsoup.parse(newsHtml)
        val newsCard = html.getElementsByClass("card-news-list__item").firstOrNull()
        val newsUrl = newsCard?.getElementsByTag("a")?.firstOrNull()?.attr("href")
        return newsUrl?.let { "https://mospolytech.ru$it" }
    }

    fun parseNewsList(newsHtml: String, firstNewsYear: Int): List<NewsPreview> {
        val html = Jsoup.parse(newsHtml)
        val newsCards = html.getElementsByClass("card-news-list__item")
        var prevDate: LocalDate? = null
        return newsCards.map { element ->
            val newsUrl = "https://mospolytech.ru" +
                    (element.getElementsByTag("a").firstOrNull()?.attr("href") ?: "")
            val imageURL = "https://mospolytech.ru" +
                    (element.getElementsByTag("img").firstOrNull()?.attr("data-src") ?: "")
            val dateStr = element.getElementsByClass("card-news__label").firstOrNull()?.text() ?: ""
            val monthDay = try {
                MonthDay.from(dateFormatter.parse(dateStr.toLowerCase()))
            } catch (e: DateTimeParseException) {
                MonthDay.now()
            } catch (e: DateTimeException) {
                MonthDay.now()
            }
            val localDate = prevDate?.let { findDate(monthDay, it) } ?: LocalDate.of(
                firstNewsYear,
                monthDay.month,
                monthDay.dayOfMonth
            )
            prevDate = localDate

            val title = element.getElementsByClass("card-news__text").firstOrNull()?.text() ?: ""
            NewsPreview(
                title,
                localDate,
                imageURL,
                newsUrl
            )
        }
    }

    private fun findDate(currentMonthDay: MonthDay, previousDate: LocalDate): LocalDate {
        val year = if (previousDate.month.value >= currentMonthDay.month.value)
            previousDate.year
        else
            previousDate.year - 1
        return LocalDate.of(year, currentMonthDay.month, currentMonthDay.dayOfMonth)
    }
}