package ru.sulgik.dnevnikx.repository.account

import ru.sulgik.dnevnikx.data.Account
import ru.sulgik.dnevnikx.data.AccountData

interface LocalAccountDataRepository {

    suspend fun setData(data: AccountData)

    suspend fun getData(account: Account): AccountData

    suspend fun getData(account: List<Account>): List<AccountData>

}