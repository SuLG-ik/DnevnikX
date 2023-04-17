package ru.sulgik.account.domain

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.sulgik.account.domain.data.Gender

class AccountAndData(
    @Embedded val account: AccountEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "accountId"
    ) val data: AccountDataEntity?,
)


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
            value = ["accountId"],
            unique = true,
        )
    ]
)
class AccountDataEntity(
    @PrimaryKey
    val accountId: String,
    val name: String,
    @ColumnInfo(defaultValue = "0")
    val gender: Gender,
)
