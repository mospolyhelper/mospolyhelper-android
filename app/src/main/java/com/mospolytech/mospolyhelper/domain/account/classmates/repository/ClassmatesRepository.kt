package com.mospolytech.mospolyhelper.domain.account.classmates.repository

import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface ClassmatesRepository {
    suspend fun getInfo(): Flow<Result<List<Classmate>>>
    suspend fun getLocalInfo(): Flow<Result<List<Classmate>>>
}