package com.mospolytech.mospolyhelper.domain.schedule.model.teacher

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Teacher(val name: String) : Parcelable {
    fun getShortName(): String {
        val names = getNames(name)
        if (names.isEmpty())
            return ""

        return if (!canBeShortened(names)) {
            names.joinToString("\u00A0")
        } else {
            val shortName = StringBuilder(names.first())
            for (i in 1 until names.size) {
                shortName.append("\u00A0")
                    .append(names[i].first())
                    .append('.')
            }
            shortName.toString()
        }
    }

    private fun canBeShortened(names: List<String>): Boolean {
        val blockWords = listOf("вакансия")
        val hasBlockWord = blockWords.all { blockWord ->
            names.any { it.contains(blockWord, true) }
        }

        val cond2 = (names.first().length > 1) && (names.first().let { it[0].isLowerCase() == it[1].isLowerCase() })

        return !hasBlockWord || cond2
    }

    private fun getNames(name: String): List<String> {
        return name.split(' ', '.')
            .filter { it.isNotEmpty() || it.isNotBlank() }
    }
}