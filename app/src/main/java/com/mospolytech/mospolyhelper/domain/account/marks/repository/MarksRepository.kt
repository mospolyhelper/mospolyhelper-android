package com.mospolytech.mospolyhelper.domain.account.marks.repository

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface MarksRepository {
    suspend fun getInfo(): Flow<Result<Marks>>
    suspend fun getLocalInfo(): Flow<Result<Marks>>
}