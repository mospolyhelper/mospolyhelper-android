package com.mospolytech.mospolyhelper.data.utilities.news.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.mospolytech.mospolyhelper.data.utilities.news.api.UniversityNewsApi
import com.mospolytech.mospolyhelper.data.utilities.news.converter.NewsConverter
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import io.ktor.utils.io.errors.*

class NewsPagingSource(
    private val api: UniversityNewsApi,
    private val converter: NewsConverter
) : PagingSource<Int, NewsPreview>() {
    companion object {
        const val NEWS_STARTING_PAGE_INDEX = 1
        const val NEWS_PAGE_SIZE = 8
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsPreview> {
        return try {
            val position = params.key ?: NEWS_STARTING_PAGE_INDEX
            val newsResponse = api.getNews(position)
            val news = converter.parseNews(newsResponse.html)
            val nextKey = if (news.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NEWS_PAGE_SIZE)
            }
            LoadResult.Page(
                data = news,
                prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, NewsPreview>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}