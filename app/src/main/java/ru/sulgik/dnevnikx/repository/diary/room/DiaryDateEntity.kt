package ru.sulgik.dnevnikx.repository.diary.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.platform.TimePeriod
import ru.sulgik.dnevnikx.repository.account.room.AccountEntity


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
class DiaryDateEntity(
    val title: String,
    @Embedded
    val alert: DiaryDateAlert?,
    val date: LocalDate,
    val accountId: String,
) {
    @ColumnInfo(index = true)
    @PrimaryKey
    var id: Long = 0
}

class DiaryDateAlert(
    val message: String,
    val alert: String,
)

class DiaryDateLessonWithMarksHomeworkFiles(
    @Embedded val lesson: DiaryDateLessonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    var homeworks: List<LessonHomeworkEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    var marks: List<LessonMarkEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    var files: List<LessonFileEntity> = emptyList(),
)

class DiaryWithLesson(
    @Embedded val diaryDate: DiaryDateEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "diaryDateId"
    )
    val lessons: List<DiaryDateLessonWithMarksHomeworkFiles>,
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DiaryDateEntity::class,
            parentColumns = ["id"],
            childColumns = ["diaryDateId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
class DiaryDateLessonEntity(
    val number: Int,
    val title: String,
    @Embedded val time: TimePeriod,
) {
    var diaryDateId: Long = 0

    @ColumnInfo(index = true)
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DiaryDateLessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
class LessonHomeworkEntity(
    val text: String,
) {
    @ColumnInfo(index = true)
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var lessonId: Long = 0
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DiaryDateLessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
class LessonFileEntity(
    val name: String,
    val url: String,
) {
    @ColumnInfo(index = true)
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var lessonId: Long = 0
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DiaryDateLessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
class LessonMarkEntity(
    val value: Int,
    val mark: String,
) {
    @ColumnInfo(index = true)
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var lessonId: Long = 0
}

class DiaryDateWithLessons(
    @Embedded var diaryDate: DiaryDateEntity,
    @Relation(
        entity = DiaryDateLessonEntity::class,
        parentColumn = "id",
        entityColumn = "diaryDateId"
    )
    var lessons: List<DiaryDateLessonWithMarksHomeworkFiles> = emptyList(),
)