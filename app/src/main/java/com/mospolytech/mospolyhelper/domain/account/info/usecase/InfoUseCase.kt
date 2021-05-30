package com.mospolytech.mospolyhelper.domain.account.info.usecase

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class InfoUseCase(
    private val repository: InfoRepository
) {
    suspend fun getInfo(): Flow<Result2<Info>> =
        repository.getInfo().onStart {
            emit(Result2.loading())
        }
    suspend fun getLocalInfo(): Flow<Result2<Info>> =
        repository.getLocalInfo().onStart {
            //emit(Result2.loading())
        }

    fun getAvatar() = repository.getAvatar()
}