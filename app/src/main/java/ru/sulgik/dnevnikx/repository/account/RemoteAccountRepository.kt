package ru.sulgik.dnevnikx.repository.account

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.GetAccountOutput

interface RemoteAccountRepository {

    suspend fun getAccount(auth: AuthScope): GetAccountOutput

}