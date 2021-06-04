package com.mospolytech.mospolyhelper.data.schedule.converter

import android.text.SpannableString
import android.text.style.URLSpan
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import com.mospolytech.mospolyhelper.domain.schedule.model.auditorium.Auditorium
import com.mospolytech.mospolyhelper.domain.schedule.model.teacher.Teacher
import java.util.*

private val titleRegex1 = Regex("""(\p{L}|\))\(""")
private val titleRegex2 = Regex("""\)(\p{L}|\()""")
private val titleRegex3 = Regex("""(\p{L})-(\p{L})""")
private val teacherRegex1 = Regex(""" -\S""")
private val teacherRegex2 = Regex("""\S- """)

fun processTitle(rawTitle: String): String {
    return rawTitle
        .trim()
        .replace(titleRegex1, "\$1 (")
        .replace(titleRegex2, ") \$1")
        .replace(titleRegex3, "\$1\u200b-\u200b\$2")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun parseTeachers(teachers: String): List<Teacher> {
    return teachers.trim()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        .split(',')
        .filter { it.isNotEmpty() }
        .map {
            Teacher(
                it.replace(teacherRegex1, " - ")
                    .replace(teacherRegex2, " - ")
                    .replace("  ", " ")
            )
        }
}

fun processAuditorium(auditorium: String, color: String, url: String): Auditorium {
    val parsedHtml = SpannableString(
        HtmlCompat.fromHtml(auditorium, HtmlCompat.FROM_HTML_MODE_LEGACY)
    )
    val rawTitle = parsedHtml.toString().trim()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val (title, type) = parseEmoji(rawTitle)
    val url2 = parsedHtml.getSpans<URLSpan>().firstOrNull()?.url ?: ""

    return Auditorium(
        title,
        type,
        color,
        if (url.isEmpty()) url2 else url
    )
}

private val emojis = listOf(
    "\uD83D\uDCF7" to "Вебинар",    // 📷
    "\uD83C\uDFE0" to "LMS",    // 🏠
    "\uD83D\uDCBB" to "Видеоконф."  // 💻
)

fun parseEmoji(raw: String): Pair<String, String> {
    val emoji = emojis.firstOrNull { raw.contains(it.first) }
    return if (emoji == null)
        Pair(raw, "")
    else
        Pair(raw.replace(emoji.first, ""), emoji.second)
}