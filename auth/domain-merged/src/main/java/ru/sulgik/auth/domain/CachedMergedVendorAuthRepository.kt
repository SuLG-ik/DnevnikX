package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.VendorOutput
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger

class CachedMergedVendorAuthRepository(
    private val localVendorAuthRepository: LocalVendorAuthRepository,
    private val remoteVendorAuthRepository: RemoteVendorAuthRepository,
) : MergedVendorAuthRepository {

    private val merger = Merger.named("Vendor")

    override suspend fun getVendors(): FlowResource<VendorOutput> {
        return merger.merged(
            localRequest = localVendorAuthRepository::getVendors,
            save = localVendorAuthRepository::saveVendors,
            remoteRequest = remoteVendorAuthRepository::getVendors,
        )
    }
}