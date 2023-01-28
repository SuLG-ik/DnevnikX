package ru.sulgik.dnevnikx.repository.auth

interface LocalAuthRepository {

    suspend fun getAuthorization(id: String): Authorization

    suspend fun getAuthorizationOrNull(): Authorization?

    suspend fun getAuthorizationOrNull(id: String): Authorization?

    suspend fun setAuthorization(authorization: Authorization)

}

class Authorization(
    val token: String,
    val id: String,
)