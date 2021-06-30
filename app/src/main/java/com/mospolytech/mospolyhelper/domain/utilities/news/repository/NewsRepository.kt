package com.mospolytech.mospolyhelper.domain.utilities.news.repository

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsStream(): Flow<PagingData<NewsPreview>>
}