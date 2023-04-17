package ru.sulgik.auth.domain.data

data class UserOutput(
    val title: String,
    val id: String,
    val token: String,
    val gender: Gender,
) {

    enum class Gender {
        MALE, FEMALE,
    }

}