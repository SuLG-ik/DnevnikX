package ru.sulgik.kacher.core

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class NamedMerger(
    private val tag: String,
) : Merger {

    override fun <T : Any> remote(
        defaultData: T?,
        save: suspend (T) -> Unit,
        remoteRequest: suspend () -> T,
    ): Flow<Resource<T>> {
        return flow {
            Napier.d("Single request loading", tag = "${tag}Merger")
            emit(Resource.loading(data = defaultData))
            try {
                val response = remoteRequest()
                Napier.d("Single request completed", tag = "${tag}Merger")
                emit(Resource.success(response))
                save(response)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Napier.d("Single request canceled", tag = "${tag}Merger")
                }
                Napier.d("Single request error", tag = "${tag}Merger")
                emit(Resource.error(e, data = defaultData))
            }
        }
    }

    override fun <T : Any> merged(
        defaultData: T?,
        localRequest: suspend () -> T?,
        save: suspend (T) -> Unit,
        remoteRequest: suspend () -> T,
    ): Flow<Resource<T>> {
        return flow {
            var data = defaultData
            Napier.d("Single request loading", tag = "${tag}Merger")
            emit(Resource.loading(data = data))
            try {
                data = localRequest()
                if (data != null) {
                    Napier.d("First request completed", tag = "${tag}Merger")
                    emit(Resource.success(data))
                } else {
                    Napier.d("First request empty", tag = "${tag}Merger")
                }
                data = remoteRequest()
                Napier.d("Second request completed", tag = "${tag}Merger")
                emit(Resource.success(data))
                save(data)
            } catch (exception: Exception) {
                if (exception is CancellationException) {
                    Napier.d("Single request canceled", tag = "${tag}Merger")
                    emit(Resource.canceled(data = data))
                } else {
                    Napier.d("Single request error", tag = "${tag}Merger")
                    emit(Resource.error(error = exception, data = data))
                }
            }
        }
    }


}