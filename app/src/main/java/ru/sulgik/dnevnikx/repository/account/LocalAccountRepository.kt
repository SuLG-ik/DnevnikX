package ru.sulgik.dnevnikx.repository.account

import kotlinx.coroutines.flow.Flow
import ru.sulgik.dnevnikx.data.Account

interface LocalAccountRepository {

    fun addAccount(account: Account)

    fun getAccounts(): Flow<List<Account>>

}