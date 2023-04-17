package ru.sulgik.account.domain.data

data class AccountData(
    val accountId: String,
    val name: String,
    val gender: Gender,
)

enum class Gender {
    MALE, FEMALE,
}


@JvmInline
value class Account(
    val id: String,
)

data class User(
    val name: String,
)