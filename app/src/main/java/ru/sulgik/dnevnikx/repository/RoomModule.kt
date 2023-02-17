package ru.sulgik.dnevnikx.repository

import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.repository.account.room.AccountDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataDao
import ru.sulgik.dnevnikx.repository.auth.room.AuthDao

@Module
class RoomModule {

    @Single
    fun bindsDatabase(
        context: Context,
    ): DnevnikXDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = DnevnikXDatabase::class.java,
            name = "dnevnikx"
        ).build()
    }

    @Single
    fun bindsAccountDao(database: DnevnikXDatabase): AccountDao {
        return database.accountDao
    }
    @Single
    fun bindsAccountDataDao(database: DnevnikXDatabase): AccountDataDao {
        return database.accountDataDao
    }
    @Single
    fun bindsAuthDao(database: DnevnikXDatabase): AuthDao {
        return database.authDao
    }

}