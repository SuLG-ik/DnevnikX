package ru.sulgik.account.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface AccountDataDao {

    @Transaction
    @Query("SELECT * FROM AccountEntity WHERE id = :accountId")
    suspend fun getDataForAccount(accountId: String): AccountAndData

    @Transaction
    @Query("SELECT * FROM AccountEntity WHERE id in (:accountIds)")
    suspend fun getDataForAccounts(accountIds: List<String>): List<AccountAndData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addData(data: AccountDataEntity)


}