package com.mospolytech.mospolyhelper.data.account.info.repository

import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.mospolytech.mospolyhelper.data.account.info.remote.InfoRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.Exception

class InfoRepositoryImpl(
    private val dataSource: InfoRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : InfoRepository {
    val klaxon = Klaxon()
    override suspend fun getInfo() = flow {
        val sessionId = prefDataSource.getString(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        emit(dataSource.get(sessionId))
    }



    override suspend fun getLocalInfo(): Flow<Result<Info>> = flow {
        val info = prefDataSource.getString(PreferenceKeys.Info, "")
        emit(
            if (info == "") {
                val sessionId = prefDataSource.getString(
                    PreferenceKeys.SessionId,
                    PreferenceDefaults.SessionId
                )
                dataSource.get(sessionId)
            } else {
                getLocal(info)
            })
    }

    suspend fun getLocal(res: String): Result<Info> {
        return try {
            Result.success(Klaxon().parse(res)!!)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun setLocalInfo(info: Info) {
        prefDataSource.setString(PreferenceKeys.Info, parse(info))
    }

    private fun parse(info: Info): String {
        return Klaxon().toJsonString(info)
    }
}