package com.mospolytech.mospolyhelper.utils

import android.util.Log
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.TAG

class StringProvider {
    companion object {
        fun getString(stringId: StringId ): String {
            val context = ContextProvider.context ?: return ""
            try {
                when (stringId) {
                    StringId.ScheduleWasntFound ->
                        return context.getString(R.string.schedule_not_found)
                    StringId.OfflineScheduleWasntFound ->
                        return context.getString(R.string.offline_schedule_not_found)
                    StringId.OfflineScheduleWasFound ->
                        return context.getString(R.string.offline_schedule_found)
                    StringId.GroupListWasntFounded ->
                        return context.getString(R.string.group_list_not_found)
                    StringId.OfflineGroupListWasntFounded ->
                        return context.getString(R.string.offline_group_list_not_found)
                    StringId.OfflineGroupListWasFounded ->
                        return context.getString(R.string.offline_group_list_found)
                    else ->
                        return ""
                }
            }
            catch (ex: Exception) {
                Log.e(TAG, "StringProvider error: $stringId", ex)
            }
            return ""
        }
    }
}

enum class StringId {
    ScheduleWasntFound,
    OfflineScheduleWasntFound,
    OfflineScheduleWasFound,
    GroupListWasntFounded,
    OfflineGroupListWasntFounded,
    OfflineGroupListWasFounded,
    Buildings
}