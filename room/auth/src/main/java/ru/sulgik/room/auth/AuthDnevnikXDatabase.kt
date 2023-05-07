package ru.sulgik.room.auth

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import ru.sulgik.auth.domain.AuthDao
import ru.sulgik.auth.domain.AuthEntity
import ru.sulgik.auth.domain.AuthVendorEntity
import ru.sulgik.auth.domain.VendorDao
import ru.sulgik.auth.domain.VendorEntity

@Database(
    entities = [
        AuthEntity::class, AuthVendorEntity::class,
        VendorEntity::class,
    ],
    version = 2, autoMigrations = [AutoMigration(from = 1, to = 2)],
)
abstract class AuthDnevnikXDatabase : RoomDatabase() {
    abstract val authDao: AuthDao
    abstract val vendorDao: VendorDao
}