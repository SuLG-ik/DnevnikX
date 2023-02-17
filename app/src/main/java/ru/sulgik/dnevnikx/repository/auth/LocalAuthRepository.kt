package ru.sulgik.dnevnikx.repository.auth

interface LocalAuthRepository {

    suspend fun getAuthorization(id: String): Authorization

    suspend fun addAuthorization(authorization: Authorization)

}

class Authorization(
    val token: String,
    val accountId: String,
)