package ru.sulgik.schedule.list.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.schedule.add.domain.ScheduleClassEntity
import ru.sulgik.schedule.domain.data.GetScheduleOutput

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM ScheduleClassEntity WHERE accountId = :accountId AND number = :number AND `group` = :group")
    fun getScheduleDate(accountId: String, number: String, group: String): ScheduleClassEntity?

    @Insert
    fun saveScheduleClass(data: ScheduleClassEntity): Long

    @Query("SELECT * FROM ScheduleEntity WHERE classId = :classId")
    fun getSchedules(classId: Long): List<ScheduleEntity>


    @Query("DELETE FROM ScheduleEntity WHERE classId = :classId")
    fun deleteSchedules(classId: Long)

    @Insert
    fun saveSchedules(data: List<ScheduleEntity>)

    @Transaction
    fun getSchedules(authScope: AuthScope, number: String, group: String): List<ScheduleEntity>? {
        val classId = getScheduleDate(authScope.id, number, group) ?: return null
        return getSchedules(classId.id)
    }

    @Transaction
    fun saveSchedules(
        authScope: AuthScope,
        number: String,
        group: String,
        data: List<GetScheduleOutput.Item>
    ) {
        val classId = getScheduleDate(authScope.id, number, group)?.id ?: saveScheduleClass(
            ScheduleClassEntity(
                accountId = authScope.id,
                number = number, group = group
            )
        )
        deleteSchedules(classId)
        saveSchedules(data.flatMap { lessonDate ->
            lessonDate.lessonGroups.flatMap { lessonGroup ->
                lessonGroup.lessons.map { lesson ->
                    ScheduleEntity(
                        date = lessonDate.date,
                        number = lessonGroup.number,
                        title = lesson.title,
                        time = lesson.time,
                        teacherName = lesson.teacher,
                        group = lesson.group,
                        classId = classId
                    )
                }
            }
        })
    }

}