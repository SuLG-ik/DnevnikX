package ru.sulgik.dnevnikx.repository.account.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.sulgik.dnevnikx.data.Account
import ru.sulgik.dnevnikx.data.AccountData

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