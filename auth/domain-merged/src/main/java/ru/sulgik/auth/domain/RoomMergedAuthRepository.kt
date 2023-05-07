package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.Authorization
import ru.sulgik.auth.domain.data.AuthorizationWithoutVendor

class RoomMergedAuthRepository(
    private val localAuthRepository: LocalAuthRepository,
    private val remoteAuthRepository: RemoteAuthRepository,
    private val remoteVendorAuthRepository: RemoteVendorAuthRepository,
) : MergedAuthRepository {

    override suspend fun getAuthorization(id: String): Authorization {
        return safeGetAuthorization(id)
    }

    override suspend fun addAuthorization(authorization: Authorization) {
        localAuthRepository.addAuthorization(authorization)
    }

    private suspend fun safeGetAuthorization(id: String): Authorization {
        return try {
            val authorization = localAuthRepository.getAuthorization(id)
            authorization
        } catch (e: VendorIsNotAppliedException) {
            val authorization =
                troubleshootVendor(e.authorization) ?: throw IllegalVendorForAccountException()
            localAuthRepository.addAuthorization(authorization)
            authorization
        }
    }

    private suspend fun troubleshootVendor(authorization: AuthorizationWithoutVendor): Authorization? {
        val vendors = remoteVendorAuthRepository.getVendors()
        vendors.vendors.forEach { vendor ->
            if (remoteAuthRepository.isUserExists(authorization.token, vendor)) {
                return Authorization(
                    token = authorization.token,
                    accountId = authorization.accountId,
                    vendor = vendor,
                )
            }
        }
        return null
    }

}

