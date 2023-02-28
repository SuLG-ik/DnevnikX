package ru.sulgik.dnevnikx.repository.diary.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [
        Index(
            value = ["id"],
            unique = true,
        ),
        Index(
            value = ["accountId"],
            unique = false,
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
    ],
    indices = [
        Index(
            value = ["id"],
            unique = true,
        ),
        Index(
            value = ["diaryDateId"],
            unique = false,
        )
    ]
)
class DiaryDateLessonEntity(
    val number: Int,
    val title: String,
    @Embedded val time: TimePeriod,
) {
    var diaryDateId: Long = 0

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
class LessonHomeworkEntity(
    val text: String,
) {
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
class LessonFileEntity(
    val name: String,
    val url: String,
) {
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
class LessonMarkEntity(
    val value: Int,
    val mark: String,
) {
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