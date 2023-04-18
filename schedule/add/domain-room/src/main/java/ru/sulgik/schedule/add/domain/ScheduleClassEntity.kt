package ru.sulgik.schedule.add.domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.sulgik.account.domain.AccountEntity

@Entity(
    indices = [
        Index(
            value = ["id"],
            unique = true,
        ),
        Index(
            value = ["accountId"],
            unique = false,
        ),
    ],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
class ScheduleClassEntity(
    val accountId: String,
    val number: String,
    val group: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}