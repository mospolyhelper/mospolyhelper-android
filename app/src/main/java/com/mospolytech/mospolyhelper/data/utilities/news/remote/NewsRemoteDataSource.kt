package com.mospolytech.mospolyhelper.data.utilities.news.remote

import com.mospolytech.mospolyhelper.data.utilities.news.api.UniversityNewsApi
import com.mospolytech.mospolyhelper.data.utilities.news.converter.NewsConverter
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import com.mospolytech.mospolyhelper.utils.Result0

class NewsRemoteDataSource(
    private val api: UniversityNewsApi,
    private val converter: NewsConverter
) {
    suspend fun getNews(page: Int): Result0<List<NewsPreview>> {
        return try {
            val newsResponse = api.getNews(page)
            val firsNewsUrl = converter.parseFirstNewsUrl(newsResponse.html)
            val firstNewsResponse = api.getNews(firsNewsUrl!!)
            val firstNewsYear = converter.parseNewsYear(firstNewsResponse)
            val news = converter.parseNewsList(newsResponse.html, firstNewsYear)
            Result0.Success(news)
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }

    suspend fun getEvents(page: Int): Result0<List<NewsPreview>> {
        return try {
            val newsResponse = api.getEvents(page)
            val firsNewsUrl = converter.parseFirstNewsUrl(newsResponse.html)
            val firstNewsResponse = api.getNews(firsNewsUrl!!)
            val firstNewsYear = converter.parseNewsYear(firstNewsResponse)
            val news = converter.parseNewsList(newsResponse.html, firstNewsYear)
            Result0.Success(news)
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}