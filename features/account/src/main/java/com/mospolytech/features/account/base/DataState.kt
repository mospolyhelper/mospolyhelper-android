package com.mospolytech.features.account.base

import com.mospolytech.features.base.BaseMutator

data class DataState<T>(val data: T? = null, val loadingState: DataLoadingState = DataLoadingState.None)

class DataMutator<T> : BaseMutator<DataState<T>>() {
    fun setData(data: T) {
        state.copy(data = data)
    }
}
