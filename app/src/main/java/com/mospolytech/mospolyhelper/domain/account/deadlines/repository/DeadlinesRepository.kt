package com.mospolytech.mospolyhelper.domain.account.deadlines.repository


import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface DeadlinesRepository {
    suspend fun getDeadlines(): Flow<Result2<List<Deadline>>>
    suspend fun getLocalInfo(): Flow<Result2<List<Deadline>>>
    suspend fun setDeadlines(deadlines: List<Deadline>): Flow<Result2<List<Deadline>>>
}