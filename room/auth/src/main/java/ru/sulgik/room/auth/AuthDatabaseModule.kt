package ru.sulgik.room.auth

import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ru.sulgik.auth.domain.AuthDao


@Module
class AuthDatabaseModule {


    @Single
    fun authDatabase(
        applicationContext: Context,
    ): AuthDnevnikXDatabase {
        return Room.databaseBuilder(applicationContext, AuthDnevnikXDatabase::class.java, "auth")
            .build()
    }

    @Single
    fun authDao(db: AuthDnevnikXDatabase): AuthDao {
        return db.authDao
    }

}