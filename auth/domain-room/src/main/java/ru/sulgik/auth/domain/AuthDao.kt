package ru.sulgik.auth.domain

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface AuthDao {

    @Query("SELECT * FROM AuthEntity WHERE accountId = :accountId")
    suspend fun getAuthForAccount(accountId: String): AuthWithVendor

    @Insert
    suspend fun insertAuth(auth: AuthEntity)

    @Update
    suspend fun updateAuth(auth: AuthEntity)

    @Insert
    suspend fun insertVendor(auth: AuthVendorEntity)


    @Query("SELECT EXISTS(SELECT * FROM AuthEntity WHERE accountId = :accountId)")
    suspend fun existsAuth(accountId: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM AuthVendorEntity WHERE region = :region)")
    suspend fun existsVendor(region: String): Boolean

    @Transaction
    suspend fun addAuth(auth: AuthWithVendor) {
        if (auth.vendor != null) {
            if (!existsVendor(auth.vendor.region)) {
                insertVendor(auth.vendor)
                Log.d("pisos", "vendor created")
            } else {
                Log.d("pisos", "vendor skiped")
            }
        } else {
            Log.d("pisos", "vendor sucked")
        }
        if (existsAuth(auth.auth.accountId)) {
            updateAuth(auth.auth)
        } else {
            insertAuth(auth.auth)
        }
    }

}