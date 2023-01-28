package ru.sulgik.dnevnikx.repository.data

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val response: ResponseWrapper<T>,
) {
    @Serializable
    class ResponseWrapper<T>(
        val state: Int,
        val error: String?,
        val result: T,
    )
}