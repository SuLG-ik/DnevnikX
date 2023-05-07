package ru.sulgik.auth.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ru.sulgik.auth.domain.data.Vendor
import ru.sulgik.images.domain.toDomain

@Dao
interface VendorDao {

    @Query("SELECT * FROM VendorEntity")
    fun getVendors(): List<VendorEntity>

    @Insert
    suspend fun addVendorsEntities(vendors: List<VendorEntity>)

    @Query("DELETE FROM VendorEntity")
    suspend fun deleteVendors()

    @Transaction
    suspend fun addVendors(vendor: List<Vendor>) {
        deleteVendors()
        addVendorsEntities(vendor.map(Vendor::toDomain))
    }

}

private fun Vendor.toDomain(): VendorEntity {
    return VendorEntity(
        region = region,
        realName = realName,
        vendor = vendor,
        host = host,
        devKey = devKey,
        logo = logo.toDomain(),
    )
}
