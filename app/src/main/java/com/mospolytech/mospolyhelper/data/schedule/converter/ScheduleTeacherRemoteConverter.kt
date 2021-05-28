package com.mospolytech.mospolyhelper.data.schedule.converter

import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTypeUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.absoluteValue

class ScheduleTeacherRemoteConverter {
    companion object {
        val map = mapOf(
            1L to "янв",
            2L to "фев",
            3L to "мар",
            4L to "апр",
            5L to "май",
            6L to "июн",
            7L to "июл",
            8L to "авг",
            9L to "сен",
            10L to "окт",
            11L to "ноя",
            12L to "дек"
        )

        private val dateFormatter = DateTimeFormatterBuilder()
            .appendPattern("d ")
            .appendText(ChronoField.MONTH_OF_YEAR, map)
            .toFormatter(Locale("ru"))

        private val regex0 = Regex("""\((.*?)\)""")

        private val regex1 = Regex("""(\p{L}|\))\(""")
        private val regex2 = Regex("""\)(\p{L}|\()""")
        private val regex3 = Regex("""(\p{L})-(\p{L})""")
        private val regex4 = Regex(""" -\S""")
        private val regex5 = Regex("""\S- """)
    }

    fun parse(scheduleString: String): Schedule {
        val parser = Jsoup.parse(scheduleString)

        val teacher = Teacher(
            parser
                .getElementsByClass("teacher-info__name")
                .first()
                .text()
                .replace(regex4, " - ")
                .replace(regex5, " - ")
                .replace("  ", " ")
        )
        val tableBody = parser.getElementsByTag("tbody").first()

        val tempList: List<MutableList<LessonPlace>> = List(7) { mutableListOf() }

        val rows = tableBody.getElementsByTag("tr")
        for (row in rows.withIndex()) {
            val cells = row.value.getElementsByTag("td")
            for (cell in cells.withIndex()) {
                val lessonDivs = cell.value.select(">div")
                val order = row.index
                val lessons = mutableListOf<Lesson>()
                for (lessonDiv in lessonDivs) {
                    val lesson = parseLesson(lessonDiv, teacher)
                    lessons.add(lesson)
                }
                tempList[(cell.index + 1) % 7].add(
                    LessonPlace(lessons, order, false)
                )
            }
        }
        return Schedule.from(tempList)
    }

    private fun parseLesson(element: Element, teacher: Teacher): Lesson {
        val emoji = element
            .select(">b")
            .map { Auditorium.parseEmoji(it.text()) }

        val auditoriums = element
            .getElementsByClass("lesson__auditory")
            .mapIndexed { index, element ->
                val em = if (index >= emoji.size) emoji.lastOrNull() ?: "" else emoji[index]
                Auditorium(em + " " + element.text(), "")
            }

        val (dateFrom, dateTo) = parseDates(element)


        var lessonTitle = element.getElementsByClass("lesson__subject").firstOrNull()?.text() ?: ""
        val match = regex0.findAll(lessonTitle).lastOrNull()
        var lessonType = match?.groups?.get(1)?.value ?: "Другое"
        if (match != null) {
            lessonTitle = lessonTitle.replace("($lessonType)", "")
        }
        lessonTitle = processTitle(lessonTitle)
        lessonType = LessonTypeUtils.fixTeacherType(lessonType, lessonTitle)


        val groups = element.getElementsByClass("lesson__group").map { it.text()!! }


        return Lesson(
            lessonTitle,
            lessonType,
            listOf(teacher),
            auditoriums,
            groups.map { Group.fromTitle(it) }.sortedBy { it.title },
            dateFrom,
            dateTo
        )
    }

    private fun parseDates(element: Element): Pair<LocalDate, LocalDate> {
        val dates = element
            .getElementsByClass("lesson__date")
            .firstOrNull()
            ?.text()
            ?.split("-")
            ?.map { parseDate(it) }

        var dateFrom: LocalDate
        var dateTo: LocalDate
        when (dates?.size) {
            null, 0 -> {
                dateFrom = LocalDate.MIN
                dateTo = LocalDate.MAX
            }
            1 -> {
                dateFrom = dates[0]

                val dateFromNextYear = dateFrom.plusYears(1)
                val dateFromPrevYear = dateFrom.minusYears(1)
                val today = LocalDate.now()
                val currentDifference = today.until(dateFrom, ChronoUnit.DAYS).absoluteValue

                if (today.until(dateFromNextYear, ChronoUnit.DAYS).absoluteValue < currentDifference) {
                    dateFrom = dateFromNextYear
                } else if (today.until(dateFromPrevYear, ChronoUnit.DAYS).absoluteValue < currentDifference) {
                    dateFrom = dateFromPrevYear
                }
                dateTo = dateFrom
            }
            else -> {
                dateFrom = dates[0]
                dateTo = dates[1]

                val dateFromNextYear = dateFrom.plusYears(1)
                val dateToNextYear = dateTo.plusYears(1)
                val currentDifference = dateFrom.until(dateTo, ChronoUnit.DAYS)

                when {
                    // Add 1 year to dateTo and check if difference lower than current or not
                    // This condition for direct order of dates
                    currentDifference.absoluteValue > dateFrom.until(
                        dateToNextYear,
                        ChronoUnit.DAYS
                    ).absoluteValue -> {
                        dateTo = dateToNextYear
                    }
                    // If previous condition is false then try do same with dateFrom
                    // This condition for wrong (reversed) order of dates, e.g. jan 12 - sep 5
                    currentDifference.absoluteValue > dateFromNextYear.until(
                        dateTo,
                        ChronoUnit.DAYS
                    ).absoluteValue -> {
                        dateFrom = dateFromNextYear
                    }
                }

                // To fix wrong (reversed) order of dates
                if (dateFrom.until(dateTo, ChronoUnit.DAYS) < 0 ) {
                    val buf = dateTo
                    dateTo = dateFrom
                    dateFrom = buf
                }

                // If date range is sep 5 - jan 12 and today is jan,
                // then year of dates will displaced by 1
                if (LocalDate.now().until(dateFrom, ChronoUnit.YEARS) > 0) {
                    dateTo = dateTo.minusYears(1)
                    dateFrom = dateFrom.minusYears(1)
                }
            }
        }
        return Pair(dateFrom, dateTo)
    }

    private fun processTitle(rawTitle: String): String {
        return rawTitle
            .trim()
            .replace(regex1, "\$1 (")
            .replace(regex2, ") \$1")
            .replace(regex3, "\$1\u200b-\u200b\$2")
            .capitalize()
    }



    private fun parseDate(dateString: String): LocalDate {
        val monthDay = MonthDay.parse(dateString.trim().toLowerCase(), dateFormatter)
        return LocalDate.of(LocalDate.now().year, monthDay.month, monthDay.dayOfMonth)
    }
}