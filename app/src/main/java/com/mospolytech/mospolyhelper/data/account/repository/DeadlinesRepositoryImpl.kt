package com.mospolytech.mospolyhelper.data.account.repository

import android.util.Log
import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.data.utils.toJson
import com.mospolytech.mospolyhelper.data.utils.toObject
import com.mospolytech.mospolyhelper.domain.account.model.deadlines.Deadline
import com.mospolytech.mospolyhelper.domain.account.model.deadlines.MyPortfolio
import com.mospolytech.mospolyhelper.domain.account.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeadlinesRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : DeadlinesRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getDeadlines(emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<List<Deadline>>(PreferenceKeys.Deadlines)?.let {
                emit(it)
            }
        }
        val res = api.getPortfolio()
            .map {
                return@map it.getDeadlines()
            }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun setDeadlines(deadlines: List<Deadline>) = flow {
        val data = deadlines.toJson()
        val portfolio = MyPortfolio(data)
        val res = api.setPortfolio(portfolio)
            .map {
                return@map it.getDeadlines()
            }
        emit(res)
    }.flowOn(ioDispatcher)

    private fun MyPortfolio.getDeadlines(): List<Deadline> {
        return try {
            val deadlines = otherInformation.toObject<List<Deadline>>()
            prefDataSource.setObject(deadlines, PreferenceKeys.Deadlines)
            deadlines
        } catch (exception: Throwable) {
            Log.e("deadlines serialization error", exception.localizedMessage.orEmpty())
            emptyList()
        }
    }


}