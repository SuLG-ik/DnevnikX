package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.VendorOutput

interface RemoteVendorAuthRepository {

    suspend fun getVendors(): VendorOutput

}