package ru.sulgik.kacher.core

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

internal class NamedMerger(
    private val tag: String,
) : Merger {

    override fun <T : Any> local(defaultData: T?, localRequest: suspend () -> T?): FlowResource<T> {
        return flow {
            Napier.d("Local request loading", tag = "${tag}Merger")
            emit(Resource.loading(data = defaultData))
            val response = withContext(Dispatchers.IO) { localRequest() }
            Napier.d("Local request completed", tag = "${tag}Merger")
            if (response == null) {
                emit(Resource.empty())
            } else {
                emit(Resource.success(response, Resource.Source.LOCAL))
            }
        }.catch {
            if (it is CancellationException) {
                Napier.d("Local request canceled", tag = "${tag}Merger")
            }
            Napier.d("Local request error", throwable = it, tag = "${tag}Merger")
            emit(Resource.error(it, data = defaultData))
        }
    }

    override fun <T : Any> local(defaultData: T?, localFlow: () -> Flow<T?>): FlowResource<T> {
        return channelFlow {
            Napier.d("Local start collecting", tag = "${tag}Merger")
            channel.send(Resource.loading(data = defaultData))
            localFlow().collectLatest {
                Napier.d("Local request collected", tag = "${tag}Merger")
                if (it == null) {
                    channel.send(Resource.empty())
                } else {
                    channel.send(Resource.success(it, Resource.Source.LOCAL))
                }
            }
        }.catch {
            if (it is CancellationException) {
                Napier.d("Local collection canceled", tag = "${tag}Merger")
            }
            Napier.d("Local collect error", throwable = it, tag = "${tag}Merger")
            emit(Resource.error(it, data = defaultData))
        }
    }

    override fun <T : Any> remote(
        defaultData: T?,
        save: (suspend (T) -> Unit)?,
        remoteRequest: suspend () -> T,
    ): Flow<Resource<T>> {
        return flow<Resource<T>> {
            Napier.d("Single request loading", tag = "${tag}Merger")
            emit(Resource.loading(data = defaultData))
            val response = withContext(Dispatchers.IO) { remoteRequest() }
            Napier.d("Single request completed", tag = "${tag}Merger")
            emit(Resource.success(response, Resource.Source.REMOTE))
            withContext(Dispatchers.IO) { save?.invoke(response) }
        }.catch {
            if (it is CancellationException) {
                Napier.d("Single request canceled", tag = "${tag}Merger")
            }
            Napier.d("Single request error", throwable = it, tag = "${tag}Merger")
            emit(Resource.error(it, data = defaultData))
        }
    }

    override fun <T : Any> merged(
        defaultData: T?,
        localRequest: suspend () -> T?,
        save: (suspend (T) -> Unit)?,
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
                withContext(Dispatchers.IO) { save?.invoke(data) }
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