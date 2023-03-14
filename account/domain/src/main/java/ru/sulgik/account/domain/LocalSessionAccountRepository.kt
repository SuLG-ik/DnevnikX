package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.AccountSession

interface LocalSessionAccountRepository {

    suspend fun getLastAccountSession(): AccountSession?

    suspend fun updateLastAccountSession(session: AccountSession?)

}