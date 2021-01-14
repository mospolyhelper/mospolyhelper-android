package com.mospolytech.mospolyhelper.domain.account.info.usecase

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class InfoUseCase(
    private val repository: InfoRepository
) {
    suspend fun getInfo(): Flow<Result<Info>> =
        repository.getInfo().onStart {
            emit(Result.loading())
        }
    suspend fun getLocalInfo(): Flow<Result<Info>> =
        repository.getLocalInfo().onStart {
            emit(Result.loading())
        }

    suspend fun setLocalInfo(info: Info) {
        repository.setLocalInfo(info)
    }
}