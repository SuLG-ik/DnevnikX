package ru.sulgik.dnevnikx.data

data class AccountData(
    val accountId: String,
    val name: String,
)


@JvmInline
value class Account(
    val id: String,
)