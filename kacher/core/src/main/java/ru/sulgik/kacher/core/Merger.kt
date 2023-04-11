package ru.sulgik.kacher.core

import kotlinx.coroutines.flow.Flow


interface Merger {


    fun <T : Any> remote(
        defaultData: T? = null,
        save: suspend (T) -> Unit,
        remoteRequest: suspend () -> T,
    ): Flow<Resource<T>>

    fun <T : Any> merged(
        defaultData: T? = null,
        localRequest: suspend () -> T?,
        save: suspend (T) -> Unit,
        remoteRequest: suspend () -> T,
    ): Flow<Resource<T>>

    companion object {

        fun named(tag: String): Merger {
            return NamedMerger(tag)
        }

    }

}
