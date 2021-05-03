package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.SavedIdsLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule

interface SavedIdsRepository {
    fun getSavedIds(): Set<UserSchedule>

    fun setSavedIds(savedIds: Set<UserSchedule>)
}