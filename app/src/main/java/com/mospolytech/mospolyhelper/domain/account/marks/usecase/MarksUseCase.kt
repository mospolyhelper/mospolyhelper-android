package com.mospolytech.mospolyhelper.domain.account.marks.usecase

import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class MarksUseCase(
    private val repository: MarksRepository
) {
    suspend fun getInfo(): Flow<Result0<Marks>> =
        repository.getInfo().onStart {
            emit(Result0.Loading)
        }
    suspend fun getLocalInfo(): Flow<Result0<Marks>> =
        repository.getLocalInfo()

}