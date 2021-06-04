package com.mospolytech.mospolyhelper.domain.schedule.utils

import androidx.core.text.HtmlCompat
import com.mospolytech.mospolyhelper.domain.schedule.model.auditorium.Auditorium
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson

private const val minCriticalTitleLength = 10
private const val minCriticalWordLength = 5

private const val vowels = "аеёиоуыэьъюя"
private const val specChars = "ьъ"

// TODO: Fix ьъ
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
        // TODO: Fix for spec chars
        if (vowels.contains(word[i], ignoreCase = true) && !specChars.contains(word[i], ignoreCase = true)) {
            // if two vowels are near or shorted word will be too short
            if (i == vowelIndex + 1 || i < 3) {
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

fun Lesson.canMergeByGroup(other: Lesson): Boolean {
    return title == other.title &&
            auditoriums == other.auditoriums &&
            teachers == other.teachers &&
            (other.dateFrom in dateFrom..dateTo ||
                    dateFrom in other.dateFrom..other.dateTo)
}

fun Lesson.mergeByGroup(other: Lesson): Lesson {
    val minDate = if (dateFrom < other.dateFrom) dateFrom else other.dateFrom
    val maxDate = if (dateTo > other.dateTo) dateTo else other.dateTo
    return copy(
        groups = (groups + other.groups).distinct().sortedBy { it.title },
        dateFrom = minDate,
        dateTo = maxDate
    )
}

val Auditorium.fullTitle: String
    get() {
        val typeText = if (type.isNotEmpty()) "($type) " else ""
        return typeText + title
    }

val Auditorium.description: String
    get() {
        val parsedTitle = HtmlCompat.fromHtml(
            title,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()
        for (pair in audMap) {
            val regex = Regex(pair.key, RegexOption.IGNORE_CASE)
            if (regex.containsMatchIn(parsedTitle)) {
                return regex.replace(parsedTitle, pair.value)
            }
        }
        return parsedTitle
    }

private val audMap = mapOf(
    """^ав\s*((\d)(\d)(.+))$""" to """Автозаводская, к. $2, этаж $3, ауд. $1""",

    """^пр\s*((\d)(\d).+)$""" to """Прянишникова, к. $2, этаж $3, ауд. $1""",
    """^пр\s*ВЦ\s*\d+\s*\(((\d)(\d).+)\)$""" to """Прянишникова, к. $2, этаж $3, ауд. $1""",
    """^пр\s(ФО[\s-]*\d+)$""" to """Прянишникова, к. 2, этаж 4, ауд. $1""",

    """^м\s*((\d)(\d).+)$""" to """Михалковская, к. $2, этаж $3, ауд. $1""",

    """^(\d)пк\s*((\d).+)$""" to """Павла Корчагина, к. $1, этаж $3, ауд. $2""",
    """^пк\s*((\d).+)$""" to """Павла Корчагина, к. 1, этаж $2, ауд. $1""",

    """^([АБВНH]|Нд)\s*(\d).+$""" to """Б. Семёновская, к. $1, этаж $2, ауд. $0""",
    """^(А)[\s-]?ОМД$""" to """Б. Семёновская, к. $1, Лаборатория обработки материалов давлением""",

    """^[_]*ПД[_]*$""" to """Проектная деятельность""",

    """^[_-]*(LMS|ЛМС)[_-]*$""" to """Обучение в ЛМС""",
    """^Обучение\s+в\s+LMS$""" to """Обучение в ЛМС""",
    """^Webex$""" to """Видеоконференция в Webex""",
    """^Webinar$""" to """Онлайн лекция в Webinar""""",

    """^м[\s\p{P}]*спорт[\s\p{P}]*зал[\p{P}]*$""" to """Михалковская, Спортзал""",
    """^Зал\s+№*(\d)[_]*$""" to """Спортивный зал №$1""",
    """^Автозаводская\s+(\d)$""" to """Спортивный зал №$1 (Автозаводская)""",
    """^(.*Измайлово.*)$""" to """$1""",

    """^ИМАШ(\sРАН)?[\s_]*$""" to """Институт машиноведения имени А. А. Благонравова РАН""",
    """^ИОНХ(\sРАН)?[\s_]*$""" to """Институт общей и неорганической химии им. Н.С. Курнакова РАН""",
    """^(.*Биоинженерии.*(РАН)?)$""" to """$1""",
    """^(.*Техноград.*)$""" to """Техноград на ВДНХ""",
    """^МИСиС$""" to """НИТУ МИСиС""",

    """^Практика$""" to """Практика""",
    """^Бизнес.кар$""" to """Группа компаний «БИЗНЕС КАР»""",
)