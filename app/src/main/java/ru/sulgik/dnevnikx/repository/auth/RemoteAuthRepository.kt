package ru.sulgik.dnevnikx.repository.auth

import ru.sulgik.dnevnikx.repository.data.UserOutput

interface RemoteAuthRepository {

    suspend fun authorize(username: String, password: String): UserOutput

}

