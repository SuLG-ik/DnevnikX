package ru.sulgik.kacher.core

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

internal class NamedMerger(
    private val tag: String,
) : Merger {

    override fun <T : Any> local(defaultData: T?, localRequest: suspend () -> T?): FlowResource<T> {
        return flow {
            Napier.d("Local request loading", tag = "${tag}Merger")
            emit(Resource.loading(data = defaultData))
            try {
                val response = withContext(Dispatchers.IO) { localRequest() }
                Napier.d("Local request completed", tag = "${tag}Merger")
                if (response == null) {
                    emit(Resource.empty())
                } else {
                    emit(Resource.success(response, Resource.Source.LOCAL))
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Napier.d("Local request canceled", tag = "${tag}Merger")
                }
                Napier.d("Local request error", throwable = e, tag = "${tag}Merger")
                emit(Resource.error(e, data = defaultData))
            }
        }
    }

    override fun <T : Any> remote(
        defaultData: T?,
        save: suspend (T) -> Unit,
        remoteRequest: suspend () -> T,
    ): Flow<Resource<T>> {
        return flow {
            Napier.d("Single request loading", tag = "${tag}Merger")
            emit(Resource.loading(data = defaultData))
            try {
                val response = withContext(Dispatchers.IO) { remoteRequest() }
                Napier.d("Single request completed", tag = "${tag}Merger")
                emit(Resource.success(response, Resource.Source.REMOTE))
                withContext(Dispatchers.IO) { save(response) }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Napier.d("Single request canceled", tag = "${tag}Merger")
                }
                Napier.d("Single request error", throwable = e, tag = "${tag}Merger")
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
            Napier.d("Merged request loading", tag = "${tag}Merger")
            emit(Resource.loading(data = data))
            try {
                data = withContext(Dispatchers.IO) { localRequest() }
                if (data != null) {
                    Napier.d("Local request completed", tag = "${tag}Merger")
                    emit(Resource.success(data, Resource.Source.LOCAL))
                } else {
                    Napier.d("Local request empty", tag = "${tag}Merger")
                }
                data =
                    withContext(Dispatchers.IO) { remoteRequest() }
                Napier.d("Remote request completed", tag = "${tag}Merger")
                emit(Resource.success(data, Resource.Source.REMOTE))
                withContext(Dispatchers.IO) { save(data) }
            } catch (exception: Exception) {
                if (exception is CancellationException) {
                    Napier.d("Merged request canceled", tag = "${tag}Merger")
                    emit(Resource.canceled(data = data))
                } else {
                    Napier.d("Merged request error", throwable = exception, tag = "${tag}Merger")
                    emit(Resource.error(error = exception, data = data))
                }
            }
        }
    }


}