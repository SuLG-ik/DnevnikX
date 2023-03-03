package ru.sulgik.dnevnikx.repository.marks.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.repository.account.room.AccountEntity

@Entity(
    indices = [
        Index(
            value = ["id"],
            unique = true,
        ),
        Index(
            value = ["accountId"],
            unique = false,
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class MarksPeriodEntity(
    val accountId: String,
    val start: LocalDate, val end: LocalDate,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}


class MarksPeriodWithLesson(
    @Embedded var period: MarksPeriodEntity,
    @Relation(
        entity = MarksLessonEntity::class,
        parentColumn = "id",
        entityColumn = "periodId"
    )
    var lessons: List<MarksLessonWithMarks>,
)

class MarksLessonWithMarks(
    @Embedded var lesson: MarksLessonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    var marks: List<MarksLessonMarkEntity> = emptyList(),
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MarksPeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["periodId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(
            value = ["id"],
            unique = true,
        ),
        Index(
            value = ["periodId"],
            unique = false,
        )
    ]
)
class MarksLessonEntity(
    val title: String,
    val average: String,
    val averageValue: Int,
) {
    var periodId: Long = 0

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MarksLessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(
            value = ["id"],
            unique = true,
        ),
        Index(
            value = ["lessonId"],
            unique = false,
        )
    ]
)
class MarksLessonMarkEntity(
    val mark: String,
    val value: Int,
    val date: LocalDate,
    val message: String?,
) {
    var lessonId: Long = 0

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}