package com.mospolytech.mospolyhelper.domain.schedule.model

class SchedulePackList(
    val schedules: Iterable<Schedule?>,
    val lessonTitles: List<String>,
    val lessonTypes: List<String>,
    val lessonTeachers: List<String>,
    val lessonGroups: List<String>,
    val lessonAuditoriums: List<String>,
)