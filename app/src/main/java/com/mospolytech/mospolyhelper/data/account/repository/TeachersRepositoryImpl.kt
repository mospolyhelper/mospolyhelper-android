package com.mospolytech.mospolyhelper.data.account.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.AccountPagingDataSource
import com.mospolytech.mospolyhelper.domain.account.model.teachers.Teacher
import com.mospolytech.mospolyhelper.domain.account.repository.TeachersRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.flow.Flow

class TeachersRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : TeachersRepository {

    override fun getTeachers(query: String): Flow<PagingData<Teacher>> {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        return Pager(
            PagingConfig(pageSize = 100, enablePlaceholders = false)
        ) {
            AccountPagingDataSource {
                api.getTeachers(query, it, sessionId)
            }
        }.flow

    }


}