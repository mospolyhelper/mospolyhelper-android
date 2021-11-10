package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.marks.Marks
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface MarksRepository {
    suspend fun getMarks(emitLocal: Boolean = true): Flow<Result0<Marks>>
}