package com.mospolytech.mospolyhelper.repository.schedule.utils

import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson

class EmptyPairsListDecorator(
    private val dailySchedule: List<Lesson>
) : List<Lesson> {
    private val offset = dailySchedule.firstOrNull()?.order ?: 0
    override val size = offset + dailySchedule.size

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

    override fun get(index: Int) = if (index < offset)
        Lesson.getEmpty(index)
    else
        dailySchedule[index - offset]

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
        val from = if (fromIndex < offset) 0 else fromIndex - offset
        val to = if (fromIndex < offset) 0 else toIndex - offset
        return EmptyPairsListDecorator(
            dailySchedule.subList(from, to)
        )
    }

    class Iterator(
        private val lessonList: List<Lesson>,
        private var curPos: Int = 0
    ): ListIterator<Lesson> {
        override fun hasNext() = lessonList.size - 1 > curPos

        override fun hasPrevious() = curPos > 0

        override fun next() = lessonList[curPos++]

        override fun nextIndex() = curPos + 1

        override fun previous() = lessonList[curPos--]

        override fun previousIndex() = curPos - 1
    }
}