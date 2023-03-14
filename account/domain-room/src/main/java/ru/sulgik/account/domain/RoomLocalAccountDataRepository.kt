package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.Account
import ru.sulgik.account.domain.data.AccountData

class RoomLocalAccountDataRepository(
    private val accountDataDao: AccountDataDao,
) : LocalAccountDataRepository {

    override suspend fun setData(data: AccountData) {
        accountDataDao.addData(AccountDataEntity(data.accountId, data.name))
    }

    override suspend fun getData(account: Account): AccountData {
        return accountDataDao.getDataForAccount(account.id).toData()
    }

    override suspend fun getData(account: List<Account>): List<AccountData> {
        return accountDataDao.getDataForAccounts(account.map { it.id })
            .map { it.toData() }
    }

    private fun AccountAndData.toData(): AccountData {
        return AccountData(account.id, data?.name ?: account.id)
    }


}
