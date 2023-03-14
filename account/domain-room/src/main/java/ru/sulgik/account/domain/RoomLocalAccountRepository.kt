package ru.sulgik.account.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sulgik.account.domain.data.Account

class RoomLocalAccountRepository(
    private val accountDao: AccountDao,
) : LocalAccountRepository {

    override fun addAccount(account: Account) {
        accountDao.addAccount(account = AccountEntity(account.id))
    }

    override fun getAccounts(): Flow<List<Account>> {
        return accountDao.getAccounts().map { accounts ->
            accounts.map { account ->
                Account(account.id)
            }
        }
    }

}