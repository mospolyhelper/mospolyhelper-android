package com.mospolytech.mospolyhelper.repository.local.dao

import android.R.string
import java.io.File


class ScheduleDao {

    companion object {
        const val CurrentExtension = ".current";
        const val OldExtension = ".backup";
        const val CustomExtension = ".custom";
        const val ScheduleFolder = "cached_schedules";
        const val SessionScheduleFolder = "session";
        const val RegularScheduleFolder = "regular";
    }

    fun readGroupList(): String {
        val backingFile = File("", "group_list")
        return backingFile.readText()
    }
}