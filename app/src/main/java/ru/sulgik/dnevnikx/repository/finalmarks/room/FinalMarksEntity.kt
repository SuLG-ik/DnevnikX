package ru.sulgik.dnevnikx.repository.finalmarks.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
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
class FinalMarksLessonEntity(
    val accountId: String,
    val title: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

class FinalMarksLessonWithMarks(
    @Embedded var lesson: FinalMarksLessonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    var marks: List<FinalMarksLessonMarkEntity> = emptyList(),
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = FinalMarksLessonEntity::class,
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
class FinalMarksLessonMarkEntity(
    val mark: String,
    val value: Int,
    val period: String,
) {
    var lessonId: Long = 0

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}