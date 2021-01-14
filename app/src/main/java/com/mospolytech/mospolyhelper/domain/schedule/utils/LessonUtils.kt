package com.mospolytech.mospolyhelper.domain.schedule.utils

import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson

private const val minCriticalTitleLength = 10
private const val minCriticalWordLength = 5

private const val vowels = "аеёиоуыэюя"

fun cutTitle(title: String): String {
    if (title.length <= minCriticalTitleLength) {
        return title
    }
    val words = title.split(' ').filter { it.isNotEmpty() }
    if (words.size == 1) {
        return words.first()
    }

    return words.joinToString(" ") { cutWord(it) }
}

private fun cutWord(word: String): String {
    if (word.length <= minCriticalWordLength) {
        return word
    }

    val vowelIndex = word.indexOfFirst { vowels.contains(it, ignoreCase = true) }
    if (vowelIndex == -1) {
        return word
    }
    for (i in vowelIndex + 1 until word.length) {
        if (vowels.contains(word[i], ignoreCase = true)) {
            // if two vowels are near
            if (i == vowelIndex + 1) {
                continue
            }
            return word.substring(0, i) + '.'
        }
    }
    return word
}

fun abbreviationFrom(title: String): String {
    val words = title.split(' ').filter { it.isNotEmpty() }
    if (words.size == 1) {
        return cutWord(words.first())
    }
    val cutWords = words.map { it.first().toUpperCase() }

    return cutWords.joinToString("")
}

fun getShortType(type: String): String {
    return type.split(' ').filter { it.isNotEmpty() }.joinToString(" ") { cutWord(it) }
}

fun Lesson.canMergeByDate(other: Lesson): Boolean {
    return order == other.order &&
            title == other.title &&
            type == other.type &&
            teachers == other.teachers &&
            auditoriums == other.auditoriums &&
            groups == other.groups &&
            (other.dateFrom in dateFrom..dateTo ||
            dateFrom in other.dateFrom..other.dateTo)
}

fun Lesson.mergeByDate(other: Lesson): Lesson {
    val minDate = if (dateFrom < other.dateFrom) dateFrom else other.dateFrom
    val maxDate = if (dateTo > other.dateTo) dateTo else other.dateTo
    return copy(dateFrom = minDate, dateTo = maxDate)
}

fun Lesson.canMergeByGroup(other: Lesson): Boolean {
    return order == other.order &&
            title == other.title &&
            auditoriums == other.auditoriums &&
            teachers == other.teachers &&
            dateFrom == other.dateFrom &&
            dateTo == other.dateTo
}

fun Lesson.mergeByGroup(other: Lesson): Lesson {
    return copy(groups = (groups + other.groups).sortedBy { it.title })
}