package ru.sulgik.dnevnikx.utils

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url
import io.ktor.http.takeFrom
import ru.sulgik.dnevnikx.data.AuthScope

public suspend inline fun HttpClient.authorizedGet(
    auth: AuthScope,
    url: Url,
    block: HttpRequestBuilder.() -> Unit = {}
): HttpResponse = get {
    this.url.takeFrom(url)
    parameter("auth_token", auth.id)
    block()
}