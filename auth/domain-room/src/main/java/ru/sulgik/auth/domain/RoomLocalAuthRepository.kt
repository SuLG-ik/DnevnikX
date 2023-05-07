package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.Authorization
import ru.sulgik.auth.domain.data.AuthorizationWithoutVendor
import ru.sulgik.auth.domain.data.Vendor
import ru.sulgik.images.domain.toData
import ru.sulgik.images.domain.toDomain

class RoomLocalAuthRepository(
    private val authDao: AuthDao,
) : LocalAuthRepository {

    override suspend fun getAuthorization(id: String): Authorization {
        val account = authDao.getAuthForAccount(id)
        if (account.vendor == null) {
            throw VendorIsNotAppliedException(
                AuthorizationWithoutVendor(
                    account.auth.token,
                    account.auth.accountId
                )
            )
        }
        return Authorization(
            token = account.auth.token, accountId = account.auth.accountId,
            vendor = Vendor(
                region = account.vendor.region,
                realName = account.vendor.realName,
                vendor = account.vendor.vendor,
                host = account.vendor.host,
                devKey = account.vendor.devKey,
                logo = account.vendor.logo.toData(),
            )
        )
    }

    override suspend fun addAuthorization(authorization: Authorization) {
        authDao.addAuth(
            AuthWithVendor(
                auth = AuthEntity(
                    accountId = authorization.accountId,
                    token = authorization.token,
                    region = authorization.vendor.region,
                ),
                vendor = AuthVendorEntity(
                    region = authorization.vendor.region,
                    vendor = authorization.vendor.vendor,
                    realName = authorization.vendor.realName,
                    host = authorization.vendor.host,
                    devKey = authorization.vendor.devKey,
                    logo = authorization.vendor.logo.toDomain(),
                )
            )
        )
    }

}