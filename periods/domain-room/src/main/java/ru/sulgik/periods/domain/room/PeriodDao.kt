package ru.sulgik.periods.domain.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.sulgik.common.platform.DatePeriod

@Dao
interface PeriodDao {

    @Transaction
    @Query("DELETE FROM HostPeriodEntity WHERE accountId in (:accountId)")
    suspend fun clearForAccounts(accountId: List<String>)

    @Transaction
    @Query("SELECT * FROM HostPeriodEntity WHERE accountId = :accountId")
    suspend fun getAllForAccount(accountId: String): List<HostPeriodWithNested>


    @Insert
    suspend fun saveHostPeriod(host: HostPeriodEntity): Long

    @Insert
    suspend fun saveHostPeriods(host: List<HostPeriodEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNestedPeriod(nested: List<NestedPeriodEntity>)

    @Transaction
    suspend fun savePeriods(
        accountId: String,
        hosts: List<HostPeriodEntity>,
        nested: List<List<DatePeriod>>,
    ) {
        clearForAccounts(hosts.map { it.accountId }.distinct())
        val hostIds = saveHostPeriods(hosts)
        val nestedPeriods = nested.flatMapIndexed { index, periods ->
            periods.map {
                NestedPeriodEntity(
                    periodId = hostIds[index],
                    start = it.start,
                    end = it.end
                )
            }
        }
        saveNestedPeriod(nestedPeriods)
    }

}