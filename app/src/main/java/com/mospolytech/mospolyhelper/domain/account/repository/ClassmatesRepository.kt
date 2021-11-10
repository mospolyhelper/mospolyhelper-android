package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.classmates.Classmate
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface ClassmatesRepository {

    suspend fun getClassmates(emitLocal: Boolean = true): Flow<Result0<List<Classmate>>>

}