package com.mospolytech.mospolyhelper.data.account.payments.repository

import com.mospolytech.mospolyhelper.data.account.payments.remote.PaymentsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PaymentsRepositoryImpl(
    private val dataSource: PaymentsRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : PaymentsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getPayments() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        res.onSuccess {
            prefDataSource.setObject(it)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalPayments() = flow {
        prefDataSource.getObject<Payments>()?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)



}