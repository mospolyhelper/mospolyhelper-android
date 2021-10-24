package com.mospolytech.mospolyhelper.data.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mospolytech.mospolyhelper.utils.PagingDto
import com.mospolytech.mospolyhelper.utils.Result0

class AccountPagingDataSource<T : Any>(
    private val query: suspend (Int) -> PagingDto<T>
): PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val key = params.key?: 1
            val data = query.invoke(key)
            LoadResult.Page(
                data.data,
                if (data.currentPage <= 1) null else data.currentPage - 1,
                if (data.currentPage < data.pageCount) data.currentPage + 1 else null
            )
        } catch (exception: Throwable) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>) =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}