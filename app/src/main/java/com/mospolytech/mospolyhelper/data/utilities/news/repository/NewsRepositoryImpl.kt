package com.mospolytech.mospolyhelper.data.utilities.news.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.data.utilities.news.remote.NewsPagingSource
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import com.mospolytech.mospolyhelper.domain.utilities.news.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl(
    private val remoteDataSource: NewsPagingSource
) : NewsRepository {
    companion object {
        const val NEWS_PAGE_SIZE = 8
    }

    override fun getNewsStream(): Flow<PagingData<NewsPreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = NEWS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { remoteDataSource }
        ).flow
    }
}