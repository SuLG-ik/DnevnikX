package ru.sulgik.dnevnikx.repository.auth.room

import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.repository.auth.Authorization
import ru.sulgik.dnevnikx.repository.auth.LocalAuthRepository

@Single
class RoomLocalAuthRepository(
    private val authDao: AuthDao,
) : LocalAuthRepository {

    override suspend fun getAuthorization(id: String): Authorization {
        val account = authDao.getAuthForAccount(id)
        return Authorization(account.token, account.accountId)
    }

    override suspend fun addAuthorization(authorization: Authorization) {
        authDao.addAuth(AuthEntity(authorization.accountId, authorization.token))
    }

}