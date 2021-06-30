package com.mospolytech.mospolyhelper.domain.utilities.news.usecase

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import com.mospolytech.mospolyhelper.domain.utilities.news.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class NewsUseCase(
    private val repository: NewsRepository
) {
    fun getNewsStream(): Flow<PagingData<NewsPreview>> {
        return repository.getNewsStream()
    }
}