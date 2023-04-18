package ru.sulgik.account.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.sulgik.account.domain.data.AccountData

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDataClasses(data: List<AccountDataClassesEntity>)

    @Transaction
    @Query("DELETE FROM AccountDataEntity WHERE accountId = :accountId")
    suspend fun deleteData(accountId: String)

    @Transaction
    suspend fun updateData(data: AccountData) {
        deleteData(data.accountId)
        addData(AccountDataEntity(data.accountId, data.name, data.gender))
        addDataClasses(data.classes.map { AccountDataClassesEntity(data.accountId, it.fullTitle) })
    }


}