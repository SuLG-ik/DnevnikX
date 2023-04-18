package ru.sulgik.schedule.add.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleClassDao {

    @Insert
    fun addClass(data: ScheduleClassEntity)

    @Insert
    fun addClasses(data: List<ScheduleClassEntity>)

    @Query("DELETE FROM ScheduleClassEntity WHERE accountId = :accountId AND number = :number  AND `group` = :group")
    fun deleteClasses(accountId: String, number: String, group: String)

    @Query("SELECT * FROM ScheduleClassEntity WHERE accountId = :accountId ORDER BY number and `group`")
    fun getClasses(accountId: String): Flow<List<ScheduleClassEntity>>

}