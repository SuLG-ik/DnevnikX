package ru.sulgik.dnevnikx.data

sealed class StateStatus {
    object Loading: StateStatus()
    object Success: StateStatus()
    object Failure: StateStatus()
}