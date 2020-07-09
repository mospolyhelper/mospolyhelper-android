package com.mospolytech.mospolyhelper.repository.schedule

import android.util.Log
import com.mospolytech.mospolyhelper.utils.StringId
import com.mospolytech.mospolyhelper.utils.StringProvider
import com.mospolytech.mospolyhelper.utils.TAG

class GroupListRepository(
    private val dao: GroupListDao
) {
    suspend fun getGroupList(downloadNew: Boolean, messageBlock: (String) -> Unit = { }): List<String> {
        var groupList: List<String>? = null
        if (downloadNew) {
            try {
                groupList = dao.download()
                try {
                    dao.save(groupList)
                } catch (e1: Exception) {
                    Log.e(TAG, "Save group list fail", e1)
                }
            } catch (e2: Exception) {
                Log.e(TAG, "Download group list fail", e2)
                try {
                    messageBlock(StringProvider.getString(StringId.GroupListWasntFounded))
                    groupList = dao.read()
                    if (groupList.isEmpty()) {
                        throw Exception("Read group list from the storage fail");
                    }
                    messageBlock(StringProvider.getString(StringId.OfflineGroupListWasFounded))
                } catch (e3: Exception) {
                    Log.e(TAG, "Read group list after it was download fail", e3)
                    messageBlock(StringProvider.getString(StringId.OfflineGroupListWasntFounded))
                    groupList = emptyList()
                }
            }
        } else {
            try {
                groupList = dao.read()
                if (groupList.isEmpty()) {
                    throw Exception("Read group list from the storage fail");
                }
                messageBlock(StringProvider.getString(StringId.OfflineGroupListWasFounded))
            } catch (e3: Exception) {
                Log.e(TAG, "Read group list after it was download fail", e3)
                messageBlock(StringProvider.getString(StringId.OfflineGroupListWasntFounded))
                groupList = emptyList()
            }
        }
        return groupList ?: emptyList()
    }
}