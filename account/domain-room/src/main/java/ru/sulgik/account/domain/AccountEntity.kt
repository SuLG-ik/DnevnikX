package ru.sulgik.account.domain

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