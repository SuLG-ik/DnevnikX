package ru.sulgik.dnevnikx.repository.account.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AccountEntity(
    @PrimaryKey
    var id: String,
)