package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.GetAccountDataOutput
import ru.sulgik.auth.core.AuthScope

interface RemoteAccountDataRepository {

    suspend fun getAccount(auth: AuthScope): GetAccountDataOutput

    suspend fun getAccounts(auths: List<AuthScope>): List<GetAccountDataOutput>

}