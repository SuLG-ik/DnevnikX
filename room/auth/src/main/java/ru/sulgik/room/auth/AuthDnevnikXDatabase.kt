package ru.sulgik.room.auth

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.sulgik.auth.domain.AuthDao
import ru.sulgik.auth.domain.AuthEntity

@Database(
    entities = [AuthEntity::class],
    version = 1,
)
abstract class AuthDnevnikXDatabase : RoomDatabase() {
    abstract val authDao: AuthDao
}