package com.mospolytech.mospolyhelper.domain.account.repository


import com.mospolytech.mospolyhelper.domain.account.model.deadlines.Deadline
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface DeadlinesRepository {
    suspend fun getDeadlines(emitLocal: Boolean = true): Flow<Result0<List<Deadline>>>
    suspend fun setDeadlines(deadlines: List<Deadline>): Flow<Result0<List<Deadline>>>
}