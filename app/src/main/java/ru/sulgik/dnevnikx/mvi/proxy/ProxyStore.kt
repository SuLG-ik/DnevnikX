package ru.sulgik.dnevnikx.mvi.proxy

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.store.Store
import ru.sulgik.dnevnikx.data.AuthScope


interface ProxyStore: Store<ProxyStore.Intent, ProxyStore.State, ProxyStore.Label> {

    sealed interface Intent

    @Parcelize
    data class State(
        val authScope: AuthScope? = null,
        val isLoading: Boolean = true,
    ): Parcelable

    sealed interface Label

}