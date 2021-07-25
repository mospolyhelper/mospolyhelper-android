package com.mospolytech.mospolyhelper.domain.account.classmates.repository

import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface ClassmatesRepository {
    suspend fun getInfo(): Flow<Result0<List<Classmate>>>
    suspend fun getLocalInfo(): Flow<Result0<List<Classmate>>>
}