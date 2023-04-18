package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.auth.core.AuthScope

class RoomLocalAccountDataRepository(
    private val accountDataDao: AccountDataDao,
) : LocalAccountDataRepository {

    override suspend fun setData(data: AccountData) {
        accountDataDao.updateData(data)
    }

    override suspend fun setData(datas: List<AccountData>) {
        datas.forEach { setData(it) }
    }

    override suspend fun getData(account: AuthScope): AccountData? {
        return accountDataDao.getDataForAccount(account.id).toData()
    }

    override suspend fun getData(account: List<AuthScope>): List<AccountData> {
        return accountDataDao.getDataForAccounts(account.map { it.id })
            .mapNotNull { it.toData() }
    }

    private fun AccountAndData.toData(): AccountData? {
        if (data == null) {
            return null
        }
        return AccountData(
            accountId = account.id,
            name = data.account.name,
            gender = data.account.gender,
            classes = data.classes.map {
                AccountData.Class(it.fullTitle)
            }
        )
    }


}
