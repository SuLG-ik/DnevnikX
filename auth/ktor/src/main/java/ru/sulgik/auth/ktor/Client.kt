package ru.sulgik.auth.ktor

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.core.VendorScope
import ru.sulgik.auth.domain.LocalAuthRepository


class Client(
    private val client: HttpClient,
    private val localAuthRepository: LocalAuthRepository,
) {

    suspend fun authorizedGet(
        auth: AuthScope,
        path: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = client.get {
        val authorization = localAuthRepository.getAuthorization(auth.id)
        url(
            host = authorization.vendor.host,
            path = "apiv3/$path",
        )
        parameter("vendor", authorization.vendor.vendor)
        parameter("devkey", authorization.vendor.devKey)
        parameter("auth_token", localAuthRepository.getAuthorization(auth.id).token)
        block()
    }

    suspend fun unauthorizedGet(
        auth: VendorScope,
        path: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = client.get {
        url(
            host = auth.host,
            path = "apiv3/$path",
        )
        parameter("vendor", auth.vendor)
        parameter("devkey", auth.devKey)
        block()
    }


}

