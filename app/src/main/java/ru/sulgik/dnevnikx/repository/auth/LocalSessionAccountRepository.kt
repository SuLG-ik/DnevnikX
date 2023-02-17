package ru.sulgik.dnevnikx.repository.auth

import ru.sulgik.dnevnikx.data.AccountSession

interface LocalSessionAccountRepository {

    suspend fun getLastAccountSession(): AccountSession?

    suspend fun updateLastAccountSession(session: AccountSession?)

}