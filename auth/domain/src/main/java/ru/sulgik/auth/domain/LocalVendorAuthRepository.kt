package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.VendorOutput

interface LocalVendorAuthRepository {

    suspend fun getVendors(): VendorOutput?

    suspend fun saveVendors(vendor: VendorOutput)
}