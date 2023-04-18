package ru.sulgik.auth.domain

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.auth.domain.data.UserOutput
import ru.sulgik.common.safeBody

class KtorRemoteAuthRepository(
    private val client: HttpClient,
) : RemoteAuthRepository {

    override suspend fun authorize(username: String, password: String): UserOutput {
        val response = client.post("auth") {
            setBody(AuthRequestBody(username, password))
        }
        val auth = response.safeBody<AuthResponseBody>()
        val user = getUser(auth.token)
        return UserOutput(
            title = user.title,
            id = user.vuid,
            token = auth.token,
            gender = user.gender.toData(),
            classes = user.classes.map {
                UserOutput.Class(it.value.fullTitle)
            }
        )
    }

    private suspend fun getUser(token: String): GetRulesResponse {
        val response = client.get("getrules") {
            parameter("auth_token", token)
        }
        return response.safeBody()
    }

}

private fun GetRulesResponse.Gender.toData(): UserOutput.Gender {
    return when (this) {
        GetRulesResponse.Gender.MALE -> UserOutput.Gender.MALE
        GetRulesResponse.Gender.FEMALE -> UserOutput.Gender.FEMALE
    }
}

@Serializable
private class GetRulesResponse(
    val roles: List<String>,
    val id: String,
    val vuid: String,
    val title: String,
    val gender: Gender = Gender.MALE,
    @SerialName("groups")
    val classes: Map<String, Class>
) {
    @Serializable
    enum class Gender {
        @SerialName("male")
        MALE,

        @SerialName("female")
        FEMALE,
    }

    @Serializable
    data class Class(
        @SerialName("name")
        val fullTitle: String,
    )
}

@Serializable
private class AuthRequestBody(
    @SerialName("login")
    val username: String,
    val password: String,
)

@Serializable
private class AuthResponseBody(
    val token: String,
)
