package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.UserOutput
import ru.sulgik.auth.domain.data.Vendor


interface RemoteAuthRepository {

    suspend fun authorize(username: String, password: String, vendor: Vendor): UserOutput


    suspend fun isUserExists(token: String, vendor: Vendor): Boolean
}

