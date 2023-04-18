package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource

interface CachedAccountDataRepository {

    fun getData(account: AuthScope): FlowResource<AccountData>

    fun getData(account: List<AuthScope>): FlowResource<List<AccountData>>

}