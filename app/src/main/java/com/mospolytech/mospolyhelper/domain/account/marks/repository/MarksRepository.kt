package com.mospolytech.mospolyhelper.domain.account.marks.repository

import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface MarksRepository {
    suspend fun getInfo(): Flow<Result2<Marks>>
    suspend fun getLocalInfo(): Flow<Result2<Marks>>
}