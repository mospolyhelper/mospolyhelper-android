package com.mospolytech.mospolyhelper.data.account.payments.repository

import com.mospolytech.mospolyhelper.data.account.payments.local.PaymentsLocalDataSource
import com.mospolytech.mospolyhelper.data.account.payments.remote.PaymentsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PaymentsRepositoryImpl(
    private val dataSource: PaymentsRemoteDataSource,
    private val localDataSource: PaymentsLocalDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : PaymentsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getPayments() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        if (res.isSuccess) localDataSource.set(res.value as Payments)
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalPayments(): Flow<Result<Payments>>{
        val payments = localDataSource.getJson()
        return flow {
                if (payments.isNotEmpty()) emit(localDataSource.get(payments))
            }.flowOn(ioDispatcher)

    }



}