package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.Authorization

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