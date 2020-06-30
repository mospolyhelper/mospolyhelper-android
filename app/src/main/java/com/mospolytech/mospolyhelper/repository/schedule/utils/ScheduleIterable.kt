package com.mospolytech.mospolyhelper.repository.schedule.utils

import com.mospolytech.mospolyhelper.repository.schedule.ScheduleDao
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule

class ScheduleIterable(
    private val groupList: Iterable<String>
): Iterable<Schedule?> {
    private val dao =
        ScheduleDao()

    override fun iterator(): Iterator<Schedule?> {
        return ScheduleIterator(
            groupList.iterator(),
            dao
        )
    }

    class ScheduleIterator(
        private var groupListIterator: Iterator<String>,
        private var dao: ScheduleDao
    ): Iterator<Schedule?> {
        private var isSessionFlag = false
        private var curGroupTitle = ""

        override fun hasNext() = groupListIterator.hasNext() || isSessionFlag

        override fun next() = try {
            if (isSessionFlag) {
                val result = dao.read(curGroupTitle, isSessionFlag)
                isSessionFlag = false
                result
            } else {
                curGroupTitle = groupListIterator.next()
                val result = dao.read(curGroupTitle, isSessionFlag)
                isSessionFlag = true
                result
            }
        } catch (e: Exception) {
            null
        }
    }
}