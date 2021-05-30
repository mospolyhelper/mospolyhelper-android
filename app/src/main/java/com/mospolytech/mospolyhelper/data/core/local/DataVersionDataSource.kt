package com.mospolytech.mospolyhelper.data.core.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mospolytech.mospolyhelper.data.core.model.DataVersion

@Dao
abstract class DataVersionDataSource() {

    @Query("SELECT * FROM DataVersion WHERE `key` = :key")
    abstract fun getVersion(key: String): DataVersion

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun setVersion(version: DataVersion)
}