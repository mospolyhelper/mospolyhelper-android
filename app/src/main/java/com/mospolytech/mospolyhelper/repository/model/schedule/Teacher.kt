package com.mospolytech.mospolyhelper.repository.model.schedule

data class Teacher(val names: List<String>) {
    companion object {
        fun fromFullName(name: String) =
            Teacher(
                name.replace(" - ", "-")
                    .replace(" -", "-")
                    .replace("- ", "-")
                    .split(" ")
                    .filter { it.isNotEmpty() }
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
            for (i in names.indices) {
                shortName.append("\u00A0")
                    .append(names[i][0])
                    .append('.')
            }
            shortName.toString()
        }
    }
}