package com.mospolytech.mospolyhelper.domain.account.marks.usecase

import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class MarksUseCase(
    private val repository: MarksRepository
) {
    suspend fun getInfo(): Flow<Result2<Marks>> =
        repository.getInfo().onStart {
            emit(Result2.loading())
        }
    suspend fun getLocalInfo(): Flow<Result2<Marks>> =
        repository.getLocalInfo().onStart {
            //emit(Result2.loading())
        }

}