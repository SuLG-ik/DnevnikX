package ru.sulgik.dnevnikx.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.sulgik.dnevnikx.repository.account.room.AccountDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataEntity
import ru.sulgik.dnevnikx.repository.account.room.AccountEntity
import ru.sulgik.dnevnikx.repository.auth.room.AuthDao
import ru.sulgik.dnevnikx.repository.auth.room.AuthEntity

@Database(entities = [AccountEntity::class, AccountDataEntity::class, AuthEntity::class], version = 1)
abstract class DnevnikXDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val authDao: AuthDao
    abstract val accountDataDao: AccountDataDao

}