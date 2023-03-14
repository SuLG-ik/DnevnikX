package ru.sulgik.room.auth

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ru.sulgik.auth.domain.AuthDao


@Module
class AuthDatabaseModule {


    @Single
    fun authDatabase(
        applicationContext: Context,
    ): AuthDnevnikXDatabase {
        val supportFactory =
            SupportFactory(/* TODO: extract passphrase */ SQLiteDatabase.getBytes("passphrase".toCharArray()))
        return Room.databaseBuilder(applicationContext, AuthDnevnikXDatabase::class.java, "auth")
            .openHelperFactory(supportFactory)
            .build()
    }

    @Single
    fun authDao(db: AuthDnevnikXDatabase): AuthDao {
        return db.authDao
    }

}