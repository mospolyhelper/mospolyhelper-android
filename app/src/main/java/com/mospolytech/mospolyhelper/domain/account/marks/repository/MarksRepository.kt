package com.mospolytech.mospolyhelper.domain.account.marks.repository

import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface MarksRepository {
    suspend fun getInfo(): Flow<Result0<Marks>>
    suspend fun getLocalInfo(): Flow<Result0<Marks>>
}