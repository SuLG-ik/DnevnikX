package ru.sulgik.dnevnikx.repository.auth.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AuthEntity(
    @PrimaryKey val accountId: String,
    val token: String,
)