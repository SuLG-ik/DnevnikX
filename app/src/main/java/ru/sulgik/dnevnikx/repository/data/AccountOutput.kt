package ru.sulgik.dnevnikx.repository.data

data class GetAccountOutput(
    val data: AccountData,
    val school: School,
    val student: Student,
) {
    enum class Gender {
        MALE, FEMALE,
    }

    data class ClassGroup(
        val title: String,
        val parallel: Int,
    )

    data class Student(
        val name: StudentName,
        val gender: Gender,
        val classGroup: ClassGroup,
    )
    data class StudentName(
        val fullname: String,
        val firstname: String,
        val lastname: String,
    )

    data class School(
        val title: String,
        val fullTitle: String,
    )

    data class AccountData(
        val name: AccountName,
        val age: Int,
        val gender: Gender,
    )
    data class AccountName(
        val fullname: String,
        val firstname: String,
        val lastname: String,
        val middlename: String,
    )
}