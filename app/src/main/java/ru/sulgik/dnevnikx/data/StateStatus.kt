package ru.sulgik.dnevnikx.data

sealed class StateStatus {
    data object Loading: StateStatus()
    data object Success: StateStatus()
    data object Failure: StateStatus()
}