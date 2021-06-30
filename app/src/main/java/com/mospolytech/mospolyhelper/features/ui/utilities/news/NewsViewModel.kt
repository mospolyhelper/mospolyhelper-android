package com.mospolytech.mospolyhelper.features.ui.utilities.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.utilities.news.model.NewsPreview
import com.mospolytech.mospolyhelper.domain.utilities.news.usecase.NewsUseCase
import kotlinx.coroutines.flow.Flow

class NewsViewModel(
    private val useCase: NewsUseCase
) : ViewModel() {
    fun getNews(): Flow<PagingData<NewsPreview>> {
        val q = useCase.getNewsStream()
        return q.cachedIn(viewModelScope)
    }

}