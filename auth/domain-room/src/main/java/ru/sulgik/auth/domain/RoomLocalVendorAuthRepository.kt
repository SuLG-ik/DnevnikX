package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.Vendor
import ru.sulgik.auth.domain.data.VendorOutput
import ru.sulgik.images.domain.toData

class RoomLocalVendorAuthRepository(
    private val vendorDao: VendorDao
) : LocalVendorAuthRepository {
    override suspend fun getVendors(): VendorOutput? {
        val vendors = vendorDao.getVendors()
        if (vendors.isEmpty()) {
            return null
        }
        return VendorOutput(
            vendors = vendors.map { vendor ->
                Vendor(
                    region = vendor.region,
                    realName = vendor.realName,
                    vendor = vendor.vendor,
                    host = vendor.host,
                    devKey = vendor.devKey,
                    logo = vendor.logo.toData(),
                )
            }
        )
    }

    override suspend fun saveVendors(vendor: VendorOutput) {
        vendorDao.addVendors(vendor.vendors)
    }
}