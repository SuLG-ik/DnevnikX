package ru.sulgik.auth.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthDao {

    @Query("SELECT * FROM AuthEntity WHERE accountId = :accountId")
    suspend fun getAuthForAccount(accountId: String): AuthEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAuth(auth: AuthEntity)

}