package ru.sulgik.account.domain

import kotlinx.coroutines.flow.Flow
import ru.sulgik.account.domain.data.AccountId

interface LocalAccountRepository {

    fun addAccount(account: AccountId)

    fun getAccounts(): Flow<List<AccountId>>

}