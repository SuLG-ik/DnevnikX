package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.VendorOutput
import ru.sulgik.kacher.core.FlowResource

interface MergedVendorAuthRepository {

    suspend fun getVendors(): FlowResource<VendorOutput>

}