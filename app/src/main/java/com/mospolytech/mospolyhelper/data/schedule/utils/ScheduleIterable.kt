package com.mospolytech.mospolyhelper.data.schedule.utils

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule

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
        private var curGroupTitle = ""

        override fun hasNext() = groupListIterator.hasNext()

        override fun next(): Schedule? {
            curGroupTitle = groupListIterator.next()
            return localDataSource.get(StudentSchedule(curGroupTitle, curGroupTitle))
        }
    }
}