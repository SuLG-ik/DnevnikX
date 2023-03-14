package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.Authorization

interface LocalAuthRepository {

    suspend fun getAuthorization(id: String): Authorization

    suspend fun addAuthorization(authorization: Authorization)

}

