package com.mospolytech.mospolyhelper.domain.account.classmates.usecase

import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class ClassmatesUseCase(
    private val repository: ClassmatesRepository
) {
    suspend fun getInfo(): Flow<Result<List<Classmate>>> =
        repository.getInfo().onStart {
            emit(Result.loading())
        }
    suspend fun getLocalInfo(): Flow<Result<List<Classmate>>> =
        repository.getLocalInfo().onStart {
            //emit(Result.loading())
        }

}