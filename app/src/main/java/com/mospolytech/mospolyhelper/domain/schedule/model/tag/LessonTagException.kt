package com.mospolytech.mospolyhelper.domain.schedule.model.tag

class LessonTagException(
    val resultMessage: LessonTagMessages
    ) : Exception(resultMessage.toString())