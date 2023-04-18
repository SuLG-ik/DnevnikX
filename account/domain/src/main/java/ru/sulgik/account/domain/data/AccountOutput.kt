package ru.sulgik.account.domain.data

data class GetAccountDataOutput(
    val id: String,
    val data: AccountData,
    val school: School,
    val student: Student,
) {
    data class ClassGroup(
        val title: String,
        val parallel: Int,
    )

    data class Student(
        val name: StudentName,
        val gender: Gender,
        val classGroup: List<ClassGroup>,
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