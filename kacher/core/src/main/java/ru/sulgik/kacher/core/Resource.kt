package ru.sulgik.kacher.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class Resource<out T> private constructor(
    val status: Status<T>,
) {
    sealed interface Status<out T> {
        val data: T?

        data class Loading<out T>(override val data: T?) : Status<T>

        data class Success<out T>(override val data: T & Any) : Status<T & Any>

        object Empty : Status<Nothing> {
            override val data: Nothing?
                get() = null
        }

        data class Error<out T>(
            val error: Exception,
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

        fun <T> success(data: T & Any): Resource<T> {
            return Resource(status = Status.Success(data = data))
        }

        fun empty(): Resource<Nothing> = Resource(status = Status.Empty)

        fun <T> error(
            error: Exception,
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
    error: (error: Exception, data: T?) -> Unit = { _, _ -> },
    empty: () -> Unit = {},
    cancel: (T?) -> Unit = {},
    dataUpdated: (T?) -> Unit,
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
            success(status.data)
        }

        is Resource.Status.Canceled -> {
            cancel(status.data)
        }
    }
    dataUpdated(status.data)
}


inline fun <T> Flow<Resource<T>>.on(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    crossinline loading: (T?) -> Unit = {},
    crossinline success: (T) -> Unit = {},
    crossinline error: (error: Exception, data: T?) -> Unit = { _, _ -> },
    crossinline empty: () -> Unit = {},
    crossinline cancel: (T?) -> Unit = {},
    crossinline dataUpdated: (T?) -> Unit = {},
) {
    onEach {
        it.on(
            loading = loading,
            success = success,
            error = error,
            empty = empty,
            cancel = cancel,
            dataUpdated = dataUpdated
        )
    }.flowOn(dispatcher)
        .launchIn(scope)
}