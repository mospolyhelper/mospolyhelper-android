package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.SavedIdsLocalDataSource

interface SavedIdsRepository {
    fun getSavedIds(): Set<Pair<Boolean, String>>

    fun setSavedIds(savedIds: Set<Pair<Boolean, String>>)
}