package ru.sulgik.account.domain.data

data class AccountData(
    val accountId: String,
    val name: String,
)


@JvmInline
value class Account(
    val id: String,
)

data class User(
    val name: String,
)