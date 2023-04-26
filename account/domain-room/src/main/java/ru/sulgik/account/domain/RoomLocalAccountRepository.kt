package ru.sulgik.account.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sulgik.account.domain.data.AccountId

class RoomLocalAccountRepository(
    private val accountDao: AccountDao,
) : LocalAccountRepository {

    override fun addAccount(account: AccountId) {
        accountDao.addAccount(account = AccountEntity(account.id))
    }

    override fun getAccounts(): Flow<List<AccountId>> {
        return accountDao.getAccounts().map { accounts ->
            accounts.map { account ->
                AccountId(account.id)
            }
        }
    }

}