package ru.sulgik.account.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAccount(account: AccountEntity)

    @Query("select * from AccountEntity")
    fun getAccounts(): Flow<List<AccountEntity>>

}