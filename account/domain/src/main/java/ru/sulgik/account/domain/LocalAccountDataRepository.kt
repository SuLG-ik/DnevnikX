package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.Account
import ru.sulgik.account.domain.data.AccountData

interface LocalAccountDataRepository {

    suspend fun setData(data: AccountData)
    suspend fun setData(datas: List<AccountData>)

    suspend fun getData(account: Account): AccountData

    suspend fun getData(account: List<Account>): List<AccountData>

}