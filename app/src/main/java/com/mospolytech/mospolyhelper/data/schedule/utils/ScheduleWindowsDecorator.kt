package com.mospolytech.mospolyhelper.data.schedule.utils

import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson

class ScheduleWindowsDecorator(
    private val dailySchedule: List<Lesson>
) : List<Lesson> {
    private val infoPositions = mutableListOf<Int>()
    override val size: Int

    val map = mutableListOf(false, false, false, false, false, false, false)

    init {
        if (dailySchedule.isNotEmpty()) {
            var prevOrder = dailySchedule.first().order
            for (lesson in dailySchedule.withIndex()) {
                if (!map[lesson.value.order]) map[lesson.value.order] = true
                if (
                    lesson.value.order == 3 &&
                    lesson.index != 0 &&
                    prevOrder == 2
                ) {
                    infoPositions.add(lesson.index)
                }
                while (lesson.value.order > prevOrder + 1) {
                    prevOrder++
                    if (infoPositions.size == 0 || infoPositions.last() != lesson.index) {
                        infoPositions.add(lesson.index)
                    }
                }
                prevOrder = lesson.value.order
            }
        }
        size = infoPositions.size + dailySchedule.size
    }


    override fun contains(element: Lesson): Boolean {
        for (e in this) {
            if (element == e) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<Lesson>): Boolean {
        for (e in elements) {
            if (!contains(e)) {
                return false
            }
        }
        return true
    }

    override fun get(index: Int): Lesson {
        var indexWithOffset = index
        for (position in infoPositions) {
            if (indexWithOffset == position) {
                return Lesson.getEmpty(-1)
            } else if (indexWithOffset < position) {
                return dailySchedule[indexWithOffset]
            }
            indexWithOffset--
        }
        return dailySchedule[indexWithOffset]
    }


    override fun indexOf(element: Lesson): Int {
        for (e in this.withIndex()) {
            if (element == e.value) {
                return e.index
            }
        }
        return -1
    }

    override fun isEmpty() = dailySchedule.isEmpty()

    override fun iterator() =
        Iterator(
            this
        )

    override fun lastIndexOf(element: Lesson): Int {
        for (e in this.asReversed().withIndex()) {
            if (element == e.value) {
                return e.index
            }
        }
        return -1
    }

    override fun listIterator() =
        Iterator(
            this
        )

    override fun listIterator(index: Int) =
        Iterator(
            this,
            index
        )

    override fun subList(fromIndex: Int, toIndex: Int): List<Lesson> {
        var fromIndexWithOffset = fromIndex
        for (position in infoPositions) {
            if (fromIndexWithOffset < position) {
                break
            }
            fromIndexWithOffset--
        }
        var toIndexWithOffset = toIndex
        for (position in infoPositions) {
            if (toIndexWithOffset < position) {
                break
            }
            toIndexWithOffset--
        }

        return ScheduleWindowsDecorator(
            dailySchedule.subList(fromIndexWithOffset, toIndexWithOffset)
        )
    }

    class Iterator(
        private val lessonList: List<Lesson>,
        private var curPos: Int = 0
    ): ListIterator<Lesson> {
        override fun hasNext() = lessonList.size > curPos

        override fun hasPrevious() = curPos > 0

        override fun next() = lessonList[curPos++]

        override fun nextIndex() = curPos + 1

        override fun previous() = lessonList[curPos--]

        override fun previousIndex() = curPos - 1
    }
}