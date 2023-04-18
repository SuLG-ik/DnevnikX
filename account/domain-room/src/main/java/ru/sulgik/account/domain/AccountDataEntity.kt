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
        entity = AccountDataEntity::class,
        parentColumn = "id",
        entityColumn = "accountId"
    ) val data: AccountDataAndClasses?,
)

class AccountDataAndClasses(
    @Embedded var account: AccountDataEntity,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "dataId"
    )
    var classes: List<AccountDataClassesEntity>,
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

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AccountDataEntity::class,
            parentColumns = ["accountId"],
            childColumns = ["dataId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(
            value = ["dataId"],
            unique = false,
        ),
        Index(
            value = ["id"],
            unique = true,
        )
    ]
)
class AccountDataClassesEntity(
    val dataId: String,
    val fullTitle: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}