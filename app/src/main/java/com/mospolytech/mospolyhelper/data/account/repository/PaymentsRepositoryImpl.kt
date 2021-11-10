package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.model.payments.Payments
import com.mospolytech.mospolyhelper.domain.account.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PaymentsRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : PaymentsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getPayments(emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<Payments>()?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getPayments()
            .onSuccess {
                prefDataSource.setObject(it)
            }
        emit(res)
    }.flowOn(ioDispatcher)



}