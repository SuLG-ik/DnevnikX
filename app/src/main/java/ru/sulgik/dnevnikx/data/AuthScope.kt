package ru.sulgik.dnevnikx.data

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
data class AuthScope(
    val id: String,
) : Parcelable


data class User(
    val name: String,
)