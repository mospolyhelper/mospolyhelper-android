package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.SavedIdsLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.repository.SavedIdsRepository

class SavedIdsRepositoryImpl(
    private val localDataSource: SavedIdsLocalDataSource
): SavedIdsRepository {
    override fun getSavedIds(): Set<Pair<Boolean, String>> {
        return localDataSource.get() ?: emptySet()
    }

    override fun setSavedIds(savedIds: Set<Pair<Boolean, String>>) {
        localDataSource.set(savedIds)
    }
}