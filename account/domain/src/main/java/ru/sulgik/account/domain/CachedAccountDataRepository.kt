package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.Account
import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.kacher.core.FlowResource

interface CachedAccountDataRepository {

    fun getData(account: Account): FlowResource<AccountData>

    fun getData(account: List<Account>): FlowResource<List<AccountData>>

}