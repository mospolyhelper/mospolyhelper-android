package com.mospolytech.mospolyhelper.data.utilities.news.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.mospolytech.mospolyhelper.data.utilities.news.api.UniversityNewsApi
import com.mospolytech.mospolyhelper.data.utilities.news.converter.NewsConverter
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import com.mospolytech.mospolyhelper.utils.Result0
import io.ktor.utils.io.errors.*

class NewsPagingSource(
    private val remote: NewsRemoteDataSource
) : PagingSource<Int, NewsPreview>() {
    companion object {
        const val NEWS_STARTING_PAGE_INDEX = 1
        const val NEWS_PAGE_SIZE = 8
    }

    /*
    Algorithm:

    0. Download all types od news
    1. Find the newest from its
    2.

     */

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsPreview> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX
        val news = remote.getNews(position)
        val events = remote.getEvents(position)
        if (news is Result0.Success) {
            val nextKey = if (news.value.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NEWS_PAGE_SIZE)
            }
            return LoadResult.Page(
                data = news.value,
                prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } else {
            return LoadResult.Error(news.exceptionOrNull() ?: Exception("Unknown exception"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsPreview>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}