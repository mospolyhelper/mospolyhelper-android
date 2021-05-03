package com.mospolytech.mospolyhelper.data.account.teachers.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.data.account.teachers.remote.TeachersRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.domain.account.teachers.repository.TeachersRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn


class TeachersRepositoryImpl(
    private val client: TeachersHerokuClient,
    private val prefDataSource: SharedPreferencesDataSource
) : TeachersRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getInfo(query: String): Flow<PagingData<Teacher>> {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        return Pager(
            PagingConfig(pageSize = 100, enablePlaceholders = false)
        ) {
            TeachersRemoteDataSource(client, sessionId, query)
        }.flow
            .flowOn(ioDispatcher)

    }


}