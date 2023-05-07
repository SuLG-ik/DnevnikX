package ru.sulgik.auth.core

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
data class AuthScope(
    val id: String,
) : Parcelable


data class VendorScope(
    val vendor: String,
    val host: String,
    val devKey: String,
)