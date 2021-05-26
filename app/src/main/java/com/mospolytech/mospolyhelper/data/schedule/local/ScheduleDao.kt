package com.mospolytech.mospolyhelper.data.schedule.local

import androidx.room.*
import com.mospolytech.mospolyhelper.data.schedule.model.*
import com.mospolytech.mospolyhelper.data.utils.toLesson
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Dao
abstract class ScheduleDao {
    @Transaction
    open suspend fun clearLessonWithFeatures() {
        clearLessonTeacherCross()
        clearLessonGroupCross()
        clearLessonAuditoriumCross()
        clearLessons()
        clearTeachers()
        clearGroups()
        clearAuditoriums()
    }
    @Query("DELETE FROM LessonDb")
    abstract suspend fun clearLessons()
    @Query("DELETE FROM TeacherDb")
    abstract suspend fun clearTeachers()
    @Query("DELETE FROM LessonTeacherCrossRef")
    abstract suspend fun clearLessonTeacherCross()
    @Query("DELETE FROM GroupDb")
    abstract suspend fun clearGroups()
    @Query("DELETE FROM LessonGroupCrossRef")
    abstract suspend fun clearLessonGroupCross()
    @Query("DELETE FROM AuditoriumDb")
    abstract suspend fun clearAuditoriums()
    @Query("DELETE FROM LessonAuditoriumCrossRef")
    abstract suspend fun clearLessonAuditoriumCross()

    @Transaction
    @Query("SELECT * FROM LessonDb")
    abstract suspend fun getAllLessons(): List<LessonWithFeaturesDb>

    @Transaction
    open suspend fun getAllLessons(filters: ScheduleFilters): Sequence<LessonWithFeaturesDb> {
        return getAllLessons().asSequence().filter {
            (filters.types.isEmpty() || filters.types.contains(it.lesson.type))
                    && (filters.titles.isEmpty() || filters.titles.contains(it.lesson.title))
                    && (filters.teachers.isEmpty() || filters.teachers.containsAll(it.teachers.map { it.teacherName }))
                    && (filters.groups.isEmpty() || filters.groups.containsAll(it.groups.map { it.groupTitle }))
                    && (filters.auditoriums.isEmpty() || filters.auditoriums.containsAll(it.auditoriums.map { it.title }))
        }
    }

    @Transaction
    open suspend fun getFilteredSchedule(filters: ScheduleFilters): Schedule {
        val lessons = getAllLessons(filters)
        val dailySchedule = MutableList(7) {
            emptyList<LessonPlace>()
        }
        val groupedLessonWithFeatures = lessons.groupBy { it.lesson.dayOfWeek }
            .mapValues {
                it.value.groupBy { LessonTime(it.lesson.order, it.lesson.isEvening) }
            }
        for (lessonWithFeatures in groupedLessonWithFeatures) {
            dailySchedule[lessonWithFeatures.key.ordinal] =
                lessonWithFeatures.value.map {
                    LessonPlace(
                        it.value.map { it.toLesson() }.sorted(),
                        LessonTime(it.key.order, it.key.isEvening)
                    )
                }.sortedBy { it.time }
        }
        return Schedule.from(dailySchedule)
    }

    @Query("SELECT * FROM ScheduleDb WHERE userScheduleId = :userScheduleId")
    abstract suspend fun getScheduleByUser(userScheduleId: String): ScheduleDb?

    @Transaction
    open suspend fun getScheduleByUser(userSchedule: UserSchedule): ScheduleDb? {
        return getScheduleByUser(userSchedule.idGlobal)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun setSchedule(scheduleDb: ScheduleDb)

    @Query("SELECT title, type FROM LessonDb")
    abstract suspend fun getLessonTitlesAndTypes(): List<LessonTitleAndTypeDb>

    @Query("SELECT * FROM TeacherDb ORDER BY teacherName")
    abstract suspend fun getTeachers(): List<TeacherDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun setTeacher(teacher: TeacherDb)

    @Query("SELECT * FROM GroupDb ORDER BY groupTitle")
    abstract suspend fun getGroups(): List<GroupDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun setGroup(group: GroupDb)

    @Query("SELECT * FROM AuditoriumDb ORDER BY title")
    abstract suspend fun getAuditoriums(): List<AuditoriumDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun setAuditorium(auditorium: AuditoriumDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun setLesson(lesson: LessonDb): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun setLessonTeacher(lessonTeacher: LessonTeacherCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun setLessonGroup(lessonGroup: LessonGroupCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun setLessonAuditorium(lessonAuditorium: LessonAuditoriumCrossRef)

    @Transaction
    open suspend fun setLessonWithFeatures(lessonWithFeaturesDb: LessonWithFeaturesDb) =
        coroutineScope {
            val lessonId = setLesson(lessonWithFeaturesDb.lesson).toInt()
            launch {
                for (teacher in lessonWithFeaturesDb.teachers) {
                    setTeacher(teacher)
                    setLessonTeacher(LessonTeacherCrossRef(lessonId, teacher.teacherName))
                }
            }
            launch {
                for (group in lessonWithFeaturesDb.groups) {
                    setGroup(group)
                    setLessonGroup(LessonGroupCrossRef(lessonId, group.groupTitle))
                }
            }
            launch {
                for (auditorium in lessonWithFeaturesDb.auditoriums) {
                    val audId = setAuditorium(auditorium).toInt()
                    setLessonAuditorium(LessonAuditoriumCrossRef(lessonId, audId))
                }
            }
        }
}