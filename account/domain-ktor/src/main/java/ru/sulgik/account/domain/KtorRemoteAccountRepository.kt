package ru.sulgik.account.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.ktor.Client
import ru.sulgik.account.domain.GetRulesBody.Gender.*
import ru.sulgik.account.domain.data.GetAccountOutput
import ru.sulgik.common.safeBody

class KtorRemoteAccountRepository(
    private val client: Client,
) : RemoteAccountRepository {
    override suspend fun getAccount(auth: AuthScope): GetAccountOutput {
        val response = client.authorizedGet(auth, "getrules")
        val body = response.safeBody<GetRulesBody>()
        val school = body.relations.schools.firstOrNull()
            ?: throw UnsupportedOperationException("school does not provided")
        val student = body.relations.students.firstNotNullOfOrNull { it.value }
            ?: throw UnsupportedOperationException("student does not provided")
        val classGroup = body.relations.groups.firstNotNullOfOrNull { it.value }
            ?: throw UnsupportedOperationException("class does not provided")
        return GetAccountOutput(
            data = GetAccountOutput.AccountData(
                name = GetAccountOutput.AccountName(
                    fullname = body.fullname,
                    firstname = body.firstname,
                    lastname = body.lastname,
                    middlename = body.middlename,
                ),
                age = body.age,
                gender = body.gender.toGender(),
            ),
            student = GetAccountOutput.Student(
                name = GetAccountOutput.StudentName(
                    fullname = student.fullname,
                    firstname = student.firstname,
                    lastname = student.lastname
                ),
                classGroup = GetAccountOutput.ClassGroup(
                    title = classGroup.name,
                    parallel = classGroup.parallel,
                ),
                gender = student.gender.toGender(),
            ),
            school = GetAccountOutput.School(
                school.title,
                school.fullTitle,
            )
        )
    }
}


private fun GetRulesBody.Gender.toGender(): GetAccountOutput.Gender {
    return when (this) {
        MALE -> GetAccountOutput.Gender.MALE
        FEMALE -> GetAccountOutput.Gender.FEMALE
    }
}

@Serializable
private class GetRulesBody(
    @SerialName("title")
    val fullname: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val middlename: String = "",
    val age: Int = 0,
    val gender: Gender = MALE,
    val relations: Relations = Relations(),
) {
    @Serializable
    enum class Gender {
        @SerialName("male")
        MALE,

        @SerialName("female")
        FEMALE,
    }

    @Serializable
    class Relations(
        val students: Map<String, Student> = emptyMap(),
        val groups: Map<String, Group> = emptyMap(),
        val schools: List<School> = emptyList(),
    )

    @Serializable
    class School(
        val title: String = "",
        @SerialName("title_full")
        val fullTitle: String = "",
    )

    @Serializable
    class Group(
        val name: String = "",
        val parallel: Int = 0,
    )

    @Serializable
    class Student(
        @SerialName("title")
        val fullname: String = "",
        val firstname: String = "",
        val lastname: String = "",
        val gender: Gender = MALE,
    )
}