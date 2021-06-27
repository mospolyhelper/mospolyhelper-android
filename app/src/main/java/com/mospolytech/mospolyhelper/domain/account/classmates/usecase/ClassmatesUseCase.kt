package com.mospolytech.mospolyhelper.domain.account.classmates.usecase

import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class ClassmatesUseCase(
    private val repository: ClassmatesRepository
) {
    suspend fun getInfo(): Flow<Result0<List<Classmate>>> =
        repository.getInfo().onStart {
            emit(Result0.Loading)
        }
    suspend fun getLocalInfo(): Flow<Result0<List<Classmate>>> =
        repository.getLocalInfo()

}