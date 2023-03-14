package ru.sulgik.account.domain

import kotlinx.coroutines.flow.Flow
import ru.sulgik.account.domain.data.Account

interface LocalAccountRepository {

    fun addAccount(account: Account)

    fun getAccounts(): Flow<List<Account>>

}