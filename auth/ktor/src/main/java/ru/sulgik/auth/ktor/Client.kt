package ru.sulgik.auth.ktor

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url
import io.ktor.http.takeFrom
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.domain.LocalAuthRepository


class Client(
    private val client: HttpClient,
    private val localAuthRepository: LocalAuthRepository,
) {

    suspend fun authorizedGet(
        auth: AuthScope,
        url: Url,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = client.get {
        this.url.takeFrom(url)
        parameter("auth_token", localAuthRepository.getAuthorization(auth.id).token)
        block()
    }

    suspend fun authorizedGet(
        auth: AuthScope,
        url: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = client.get {
        url(url)
        parameter("auth_token", localAuthRepository.getAuthorization(auth.id).token)
        block()
    }


}

