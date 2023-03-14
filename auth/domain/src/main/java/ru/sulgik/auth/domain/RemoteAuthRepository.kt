package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.UserOutput


interface RemoteAuthRepository {

    suspend fun authorize(username: String, password: String): UserOutput

}

