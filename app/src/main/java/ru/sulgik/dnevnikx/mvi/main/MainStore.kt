package ru.sulgik.dnevnikx.mvi.main

import com.arkivanov.mvikotlin.core.store.Store
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.ParametrizedStore

interface MainStore: ParametrizedStore<MainStore.Intent, MainStore.State, MainStore.Label, MainStore.Params> {

    data class Params(
        val authScope: AuthScope?,
    )

    sealed interface Intent {

        data class ReAuth(
            val authScope: AuthScope,
        ): Intent

    }

    data class State(
        val authScope: AuthScope?,
    )

    sealed interface Label

}