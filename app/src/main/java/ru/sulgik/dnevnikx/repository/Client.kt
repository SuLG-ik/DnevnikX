package ru.sulgik.dnevnikx.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url
import io.ktor.http.takeFrom
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.auth.LocalAuthRepository


@Single
class Client(
    @Named("unauthorized")
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

