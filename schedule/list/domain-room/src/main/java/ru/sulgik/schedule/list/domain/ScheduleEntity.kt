package ru.sulgik.schedule.list.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.TimePeriod
import ru.sulgik.schedule.add.domain.ScheduleClassEntity

@Entity(
    indices = [
        Index(
            value = ["id"],
            unique = true,
        ),
        Index(
            value = ["classId"],
            unique = false,
        ),
    ],
    foreignKeys = [
        ForeignKey(
            entity = ScheduleClassEntity::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
class ScheduleEntity(
    val date: LocalDate,
    val number: String,
    val title: String,
    @Embedded val time: TimePeriod,
    val teacherName: String,
    var group: String?,
    var classId: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}