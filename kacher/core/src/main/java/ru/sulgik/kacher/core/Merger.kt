package ru.sulgik.kacher.core

import kotlinx.coroutines.flow.Flow


interface Merger {


    fun <T : Any> remote(
        defaultData: T? = null,
        save: (suspend (T) -> Unit)? = null,
        remoteRequest: suspend () -> T,
    ): FlowResource<T>

    fun <T : Any> local(
        defaultData: T? = null,
        localRequest: suspend () -> T?,
    ): FlowResource<T>

    fun <T : Any> local(
        defaultData: T? = null,
        localFlow: () -> Flow<T?>,
    ): FlowResource<T>

    fun <T : Any> merged(
        defaultData: T? = null,
        localRequest: suspend () -> T?,
        save: (suspend (T) -> Unit)? = null,
        remoteRequest: suspend () -> T,
    ): FlowResource<T>

    companion object {

        fun named(tag: String): Merger {
            return NamedMerger(tag)
        }

    }

}
