package com.mospolytech.mospolyhelper.data.schedule.repository

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.local.GroupListLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupListRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository
import com.mospolytech.mospolyhelper.utils.StringId
import com.mospolytech.mospolyhelper.utils.StringProvider
import com.mospolytech.mospolyhelper.utils.TAG

class GroupListRepositoryImpl(
    private val localDataSource: GroupListLocalDataSource,
    private val remoteDataSource: GroupListRemoteDataSource
) : GroupListRepository {

    override suspend fun getGroupList(refresh: Boolean): List<String> {
        var groupList: List<String> = emptyList()
        if (refresh) {
            try {
                groupList = remoteDataSource.get()
            } catch (e: Exception) {
                Log.e(TAG, "Download group list fail", e)
            }
        } else {
            try {
                groupList = localDataSource.get()
            } catch (e: Exception) {
                Log.e(TAG, "Read group list after it was download fail", e)
            }
        }
        return groupList
    }
}