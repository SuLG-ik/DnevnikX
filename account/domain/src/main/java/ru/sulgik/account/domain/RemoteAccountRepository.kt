package ru.sulgik.account.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.account.domain.data.GetAccountOutput

interface RemoteAccountRepository {

    suspend fun getAccount(auth: AuthScope): GetAccountOutput

}