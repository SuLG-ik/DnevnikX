package ru.sulgik.auth.domain

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sulgik.auth.domain.data.UserOutput
import ru.sulgik.auth.domain.data.Vendor
import ru.sulgik.common.safeBody

class KtorRemoteAuthRepository(
    private val client: HttpClient,
) : RemoteAuthRepository {

    override suspend fun authorize(username: String, password: String, vendor: Vendor): UserOutput {
        val response = client.post {
            url(
                scheme = "https",
                host = vendor.host,
                path = "apiv3/auth",
            )
            parameter("vendor", vendor.vendor)
            parameter("devkey", vendor.devKey)
            setBody(AuthRequestBody(username, password))
        }
        if (response.status == HttpStatusCode.Forbidden) {
            throw IllegalVendorForAccountException()
        }
        val auth = response.safeBody<AuthResponseBody>()
        val user = getUser(auth.token, vendor)
        return UserOutput(
            title = user.title,
            id = user.vuid,
            token = auth.token,
            gender = user.gender.toData(),
            classes = user.relations.classes.map {
                UserOutput.Class(it.value.fullTitle)
            }
        )
    }

    private suspend fun getUser(token: String, vendor: Vendor): GetRulesResponse {
        val response = client.get {
            url(
                scheme = "https",
                host = vendor.host,
                path = "apiv3/getrules",
            )
            parameter("vendor", vendor.vendor)
            parameter("devkey", vendor.devKey)
            parameter("auth_token", token)
        }
        if (response.status == HttpStatusCode.Forbidden) {
            throw IllegalVendorForAccountException()
        }
        return response.safeBody()
    }

    override suspend fun isUserExists(token: String, vendor: Vendor): Boolean {
        val response = client.get {
            url(
                scheme = "https",
                host = vendor.host,
                path = "apiv3/getrules",
            )
            parameter("vendor", vendor.vendor)
            parameter("devkey", vendor.devKey)
            parameter("auth_token", token)
        }
        return response.status == HttpStatusCode.OK
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
    val relations: Relations,
    val gender: Gender = Gender.MALE,
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
private data class Relations(
    @SerialName("groups")
    val classes: Map<String, GetRulesResponse.Class>
)

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
