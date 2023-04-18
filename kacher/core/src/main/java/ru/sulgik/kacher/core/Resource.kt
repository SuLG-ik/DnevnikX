package ru.sulgik.kacher.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

typealias FlowResource<T> = Flow<Resource<T>>

class Resource<out T> private constructor(
    val status: Status<T>,
) {
    enum class Source {
        LOCAL, REMOTE,
    }

    sealed interface Status<out T> {
        val data: T?

        data class Loading<out T>(override val data: T?) : Status<T>

        data class Success<out T>(
            override val data: T & Any,
            val source: Source,
        ) : Status<T & Any>

        object Empty : Status<Nothing> {
            override val data: Nothing?
                get() = null
        }

        data class Error<out T>(
            val error: Throwable,
            override val data: T?,
        ) : Status<T>

        data class Canceled<out T>(
            override val data: T?,
        ) : Status<T>
    }

    companion object {
        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(status = Status.Loading(data = data))
        }

        fun <T> success(
            data: T & Any,
            source: Source,
        ): Resource<T> {
            return Resource(status = Status.Success(data = data, source = source))
        }

        fun empty(): Resource<Nothing> = Resource(status = Status.Empty)

        fun <T> error(
            error: Throwable,
            data: T?,
        ): Resource<T> {
            return Resource(
                status = Status.Error(
                    error = error,
                    data = data
                )
            )
        }

        fun <T> canceled(
            data: T?,
        ): Resource<T> {
            return Resource(
                status = Status.Canceled(
                    data = data
                )
            )
        }
    }
}

inline fun <T> Resource<T>.on(
    loading: (T?) -> Unit = {},
    success: (T) -> Unit = {},
    successLocal: (T) -> Unit = {},
    successRemote: (T) -> Unit = {},
    error: (error: Throwable, data: T?) -> Unit = { _, _ -> },
    empty: () -> Unit = {},
    cancel: (T?) -> Unit = {},
    statusUpdated: (Resource.Status<T>) -> Unit,
) {
    when (status) {
        Resource.Status.Empty -> {
            empty()
        }

        is Resource.Status.Error -> {
            error(status.error, status.data)
        }

        is Resource.Status.Loading -> {
            loading(status.data)
        }

        is Resource.Status.Success -> {
            when (status.source) {
                Resource.Source.LOCAL -> successLocal(status.data)
                Resource.Source.REMOTE -> successRemote(status.data)
            }
            success(status.data)
        }

        is Resource.Status.Canceled -> {
            cancel(status.data)
        }
    }
    statusUpdated(status)
}


suspend fun <T> Flow<Resource<T>>.on(
    loading: suspend (T?) -> Unit = {},
    success: suspend (T) -> Unit = {},
    successLocal: suspend (T) -> Unit = {},
    successRemote: suspend (T) -> Unit = {},
    error: suspend (error: Throwable, data: T?) -> Unit = { _, _ -> },
    empty: suspend () -> Unit = {},
    cancel: suspend (T?) -> Unit = {},
    statusUpdated: suspend (status: Resource.Status<T>) -> Unit = {},
) {
    collectLatest { resource ->
        resource.on(
            loading = {
                loading(it)
            },
            success = {
                success(it)
            },
            successLocal = {
                successLocal(it)
            },
            successRemote = {
                successRemote(it)
            },
            error = { error, data ->
                error(error, data)
            },
            empty = {
                empty()
            },
            cancel = {
                cancel(it)
            },
            statusUpdated = {
                statusUpdated(it)
            }
        )
    }
}