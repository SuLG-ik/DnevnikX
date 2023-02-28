package ru.sulgik.dnevnikx.repository.account.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(
            value = ["id"],
            unique = true,
        )
    ]
)
class AccountEntity(
    @PrimaryKey
    var id: String,
)