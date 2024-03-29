package ru.sulgik.main.mvi

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.store.Store
import ru.sulgik.auth.core.AuthScope


interface MainWithSplashStore :
    Store<MainWithSplashStore.Intent, MainWithSplashStore.State, MainWithSplashStore.Label> {

    sealed interface Intent

    @Parcelize
    data class State(
        val authScope: AuthScope? = null,
        val isLoading: Boolean = true,
    ) : Parcelable

    sealed interface Label

}