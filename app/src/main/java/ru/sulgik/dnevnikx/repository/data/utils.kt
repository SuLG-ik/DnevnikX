package ru.sulgik.dnevnikx.repository.data

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseException(
    val error: String,
    val state: Int,
) : Exception(error)

@Serializable
data class ErrorResponseBody(
    val errorCode: String,
    val errorText: String,
)

@Serializable
class EmptyErrorResponseBody

suspend inline fun <reified Result> HttpResponse.safeBody(): Result {
    if (status.isSuccess()) {
        return body<Response<Result>>().response.result
    }
    val error = body<Response<EmptyErrorResponseBody>>()
    if (error.response.error == null) {
        throw ErrorResponseException(
            error = "Unknwon error",
            state = error.response.state,
        )
    }
    throw ErrorResponseException(
        error = error.response.error,
        state = error.response.state
    )
}