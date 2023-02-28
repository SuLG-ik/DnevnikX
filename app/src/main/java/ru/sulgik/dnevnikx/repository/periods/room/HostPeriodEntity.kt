package ru.sulgik.dnevnikx.repository.periods.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.LocalDate
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
class HostPeriodEntity(
    var title: String,
    var start: LocalDate,
    var end: LocalDate,
    var accountId: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}


class HostPeriodWithNested(
    @Embedded
    var host: HostPeriodEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "periodId"
    )
    var nested: List<NestedPeriodEntity> = emptyList(),
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = HostPeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["periodId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class NestedPeriodEntity(
    var periodId: Long,
    var start: LocalDate,
    var end: LocalDate,
) {
    @ColumnInfo(index = true)
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}