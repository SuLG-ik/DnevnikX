package ru.sulgik.account.domain.data

import ru.sulgik.auth.core.AuthScope

data class AccountData(
    val accountId: String,
    val name: String,
    val gender: Gender,
    val classes: List<Class>,
) {
    class Class(
        val fullTitle: String,
    )
}

enum class Gender {
    MALE, FEMALE,
}


@JvmInline
value class Account(
    val id: String,
)

fun Account.toAuthScope(): AuthScope {
    return AuthScope(id)
}

data class User(
    val name: String,
)