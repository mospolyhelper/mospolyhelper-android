package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import com.mospolytech.mospolyhelper.R

enum class LessonTagColors(val colorId: Int, val textColorId: Int) {
    ColorDefault(R.color.featureBackground, R.color.featureText),
    ColorRed(R.color.lessonTagRed, R.color.lessonTagRedText),
    ColorOrange(R.color.lessonTagOrange, R.color.lessonTagOrangeText),
    ColorYellow(R.color.lessonTagYellow, R.color.lessonTagYellowText),
    ColorGreen(R.color.lessonTagGreen, R.color.lessonTagGreenText),
    ColorLightBlue(R.color.lessonTagLightBlue, R.color.lessonTagLightBlueText),
    ColorBlue(R.color.lessonTagBlue, R.color.lessonTagBlueText),
    ColorPurple(R.color.lessonTagPurple, R.color.lessonTagPurpleText),
    ColorPink(R.color.lessonTagPink, R.color.lessonTagPinkText)
}