package ru.sulgik.dnevnikx.repository.account.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.Account
import ru.sulgik.dnevnikx.repository.account.LocalAccountRepository

@Single(binds = [LocalAccountRepository::class])
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