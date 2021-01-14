package com.mospolytech.mospolyhelper.data.account.info.repository

import com.mospolytech.mospolyhelper.data.account.info.remote.InfoRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.flow.flow

class InfoRepositoryImpl(
    private val dataSource: InfoRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : InfoRepository {
    override suspend fun getInfo() = flow {
        val sessionId = prefDataSource.getString(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        emit(dataSource.get(sessionId))
    }
}