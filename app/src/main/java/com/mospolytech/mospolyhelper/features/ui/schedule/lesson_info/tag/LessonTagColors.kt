package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag

enum class LessonTagColors(val colorId: Int, val textColorId: Int) {
    ColorDefault(R.color.featureBackground, R.color.featureText),
    ColorRed(R.color.lessonTagRed, R.color.lessonTagRedText),
    ColorBrown(R.color.lessonTagBrown, R.color.lessonTagBrownText),
    ColorOrange(R.color.lessonTagOrange, R.color.lessonTagOrangeText),
    ColorGreen(R.color.lessonTagGreen, R.color.lessonTagGreenText),
    ColorLightBlue(R.color.lessonTagLightBlue, R.color.lessonTagLightBlueText),
    ColorPurple(R.color.lessonTagPurple, R.color.lessonTagPurpleText),
    ColorPink(R.color.lessonTagPink, R.color.lessonTagPinkText)
}
//     ColorBlue(R.color.lessonTagBlue, R.color.lessonTagBlueText),

fun LessonTag.getColor(): LessonTagColors {
    val colors = LessonTagColors.values()
    val colorIndex = this.color
    if (colorIndex >= colors.size) return LessonTagColors.ColorDefault
    return colors[colorIndex]
}