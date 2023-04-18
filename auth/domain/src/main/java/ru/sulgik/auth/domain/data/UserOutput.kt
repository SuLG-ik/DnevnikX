package ru.sulgik.auth.domain.data

data class UserOutput(
    val title: String,
    val id: String,
    val token: String,
    val gender: Gender,
    val classes: List<Class>
) {

    data class Class(
        val fullTitle: String,
    )

    enum class Gender {
        MALE, FEMALE,
    }

}