package com.mospolytech.mospolyhelper.domain.account.classmates.repository

import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface ClassmatesRepository {
    suspend fun getInfo(): Flow<Result2<List<Classmate>>>
    suspend fun getLocalInfo(): Flow<Result2<List<Classmate>>>
}