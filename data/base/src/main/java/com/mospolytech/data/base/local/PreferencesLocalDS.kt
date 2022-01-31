package com.mospolytech.data.base.local

import com.mospolytech.data.base.model.PreferenceDao
import kotlinx.coroutines.flow.Flow
import org.kodein.db.DB
import org.kodein.db.flowOf
import org.kodein.db.getById
import org.kodein.db.keyById

class PreferencesLocalDS(
    private val db: DB
) : PreferencesDS {

    override fun setJson(str: String, key: String) {
        db.put(PreferenceDao(key, str))
    }

    override fun getJson(key: String): Result<PreferenceDao?> {
        return kotlin.runCatching {
            db.getById(key)
        }
    }

    override fun flowOfPreferences(key: String): Flow<PreferenceDao?> {
        return db.flowOf(db.keyById<PreferenceDao>(key))
    }
}