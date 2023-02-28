package ru.sulgik.dnevnikx.repository.account.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AccountEntity(
    @ColumnInfo(index = true)
    @PrimaryKey
    var id: String,
)