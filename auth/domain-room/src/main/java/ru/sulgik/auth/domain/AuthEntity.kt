package ru.sulgik.auth.domain

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(
            value = ["accountId"],
            unique = true,
        )
    ]
)
class AuthEntity(
    @PrimaryKey val accountId: String,
    val token: String,
)