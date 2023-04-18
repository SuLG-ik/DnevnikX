package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.auth.core.AuthScope

interface LocalAccountDataRepository {

    suspend fun setData(data: AccountData)

    suspend fun setData(datas: List<AccountData>)

    suspend fun getData(account: AuthScope): AccountData?

    suspend fun getData(account: List<AuthScope>): List<AccountData>

}