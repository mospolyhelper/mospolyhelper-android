package com.mospolytech.mospolyhelper.domain.account.info.usecase

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class InfoUseCase(
    private val repository: InfoRepository
) {
    suspend fun getInfo(): Flow<Result0<Info>> =
        repository.getInfo().onStart {
            emit(Result0.Loading)
        }
    suspend fun getLocalInfo(): Flow<Result0<Info>> =
        repository.getLocalInfo()
}