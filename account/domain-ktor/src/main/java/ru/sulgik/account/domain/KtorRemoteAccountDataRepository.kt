package ru.sulgik.account.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.account.domain.GetRulesBody.Gender.*
import ru.sulgik.account.domain.data.Gender
import ru.sulgik.account.domain.data.GetAccountDataOutput
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.ktor.Client
import ru.sulgik.common.safeBody

class KtorRemoteAccountDataRepository(
    private val client: Client,
) : RemoteAccountDataRepository {
    override suspend fun getAccount(auth: AuthScope): GetAccountDataOutput {
        val response = client.authorizedGet(auth, "getrules")
        val body = response.safeBody<GetRulesBody>()
        val school = body.relations.schools.firstOrNull()
            ?: throw UnsupportedOperationException("school does not provided")
        val student = body.relations.students.firstNotNullOfOrNull { it.value }
            ?: throw UnsupportedOperationException("student does not provided")
        val classGroup = body.relations.groups
        return GetAccountDataOutput(
            id = body.id,
            data = GetAccountDataOutput.AccountData(
                name = GetAccountDataOutput.AccountName(
                    fullname = body.fullname,
                    firstname = body.firstname,
                    lastname = body.lastname,
                    middlename = body.middlename,
                ),
                age = body.age,
                gender = body.gender.toGender(),
            ),
            student = GetAccountDataOutput.Student(
                name = GetAccountDataOutput.StudentName(
                    fullname = student.fullname,
                    firstname = student.firstname,
                    lastname = student.lastname
                ),
                classGroup = classGroup.map {
                    GetAccountDataOutput.ClassGroup(
                        title = it.value.name,
                        parallel = it.value.parallel,
                    )
                },
                gender = student.gender.toGender(),
            ),
            school = GetAccountDataOutput.School(
                school.title,
                school.fullTitle,
            )
        )
    }

    override suspend fun getAccounts(auths: List<AuthScope>): List<GetAccountDataOutput> {
        return coroutineScope {
            auths.map { async { getAccount(it) } }.awaitAll()
        }
    }
}


private fun GetRulesBody.Gender.toGender(): Gender {
    return when (this) {
        MALE -> Gender.MALE
        FEMALE -> Gender.FEMALE
    }
}

@Serializable
private class GetRulesBody(
    @SerialName("vuid")
    val id: String,
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