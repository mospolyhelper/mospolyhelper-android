package com.mospolytech.mospolyhelper.data.schedule.utils

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule

class ScheduleIterable(
    private val groupList: Iterable<String>
): Iterable<Schedule?> {
    private val dataSource = ScheduleLocalDataSource()

    override fun iterator(): Iterator<Schedule?> {
        return ScheduleIterator(
            groupList.iterator(),
            dataSource
        )
    }

    class ScheduleIterator(
        private var groupListIterator: Iterator<String>,
        private var localDataSource: ScheduleLocalDataSource
    ): Iterator<Schedule?> {
        private var isSessionFlag = false
        private var curGroupTitle = ""

        override fun hasNext() = groupListIterator.hasNext() || isSessionFlag

        override fun next(): Schedule? {
            if (!isSessionFlag) {
                curGroupTitle = groupListIterator.next()
            }
            val result = localDataSource.get(curGroupTitle, isSessionFlag)
            isSessionFlag = !isSessionFlag
            return result
        }
    }
}