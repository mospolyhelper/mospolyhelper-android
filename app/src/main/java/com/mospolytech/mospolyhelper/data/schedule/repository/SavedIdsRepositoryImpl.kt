package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.SavedIdsLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.repository.SavedIdsRepository

class SavedIdsRepositoryImpl(
    private val localDataSource: SavedIdsLocalDataSource
): SavedIdsRepository {
    override fun getSavedIds(): Set<UserSchedule> {
        return localDataSource.get() ?: emptySet()
    }

    override fun setSavedIds(savedIds: Set<UserSchedule>) {
        localDataSource.set(savedIds)
    }
}