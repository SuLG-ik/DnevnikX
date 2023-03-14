package ru.sulgik.main.mvi

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.ParametrizedStore

interface MainStore :
    ParametrizedStore<MainStore.Intent, MainStore.State, MainStore.Label, MainStore.Params> {

    data class Params(
        val authScope: AuthScope?,
    )

    sealed interface Intent {

        data class ReAuth(
            val authScope: AuthScope,
        ) : Intent

    }

    data class State(
        val authScope: AuthScope?,
    )

    sealed interface Label


}