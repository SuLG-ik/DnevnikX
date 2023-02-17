package ru.sulgik.dnevnikx.repository.account.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

class AccountAndData(
    @Embedded val account: AccountEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "accountId"
    ) val data: AccountDataEntity?,
)

@Entity
class AccountDataEntity(
    @PrimaryKey
    val accountId: String,
    val name: String,
)