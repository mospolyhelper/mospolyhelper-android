package com.mospolytech.mospolyhelper.domain.schedule.model

data class Teacher(val names: List<String>) {
    companion object {
        fun fromFullName(name: String) =
            Teacher(
                StringBuilder(name).apply {
                    var idx = indexOf(" - ")
                    while (idx != -1) {
                        replace(idx, idx + 4, "-")
                        idx = indexOf(" - ")
                    }
                    idx = indexOf(" -")
                    while (idx != -1) {
                        replace(idx, idx + 3, "-")
                        idx = indexOf(" -")
                    }
                    idx = indexOf("- ")
                    while (idx != -1) {
                        replace(idx, idx + 3, "-")
                        idx = indexOf(" -")
                    }
                }.split(' ', '.')
                    .filter { it.isNotEmpty() || it.isNotBlank() }
            )
    }

    fun getFullName() = names.joinToString(" ")

    fun getShortName(): String {
        if (names.isEmpty())
            return ""

        val isVacancy = names.any { it.contains("вакансия", true) }

        return if (isVacancy || (names.first().length > 1) && (names.first().let { it[0].isLowerCase() == it[1].isLowerCase() })) {
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
}