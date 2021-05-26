package com.mospolytech.mospolyhelper.domain.account.classmates.usecase

import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class ClassmatesUseCase(
    private val repository: ClassmatesRepository
) {
    suspend fun getInfo(): Flow<Result2<List<Classmate>>> =
        repository.getInfo().onStart {
            emit(Result2.loading())
        }
    suspend fun getLocalInfo(): Flow<Result2<List<Classmate>>> =
        repository.getLocalInfo().onStart {
            //emit(Result2.loading())
        }

}