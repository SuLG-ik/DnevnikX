package ru.sulgik.dnevnikx

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun setupNapier() {
    Napier.base(DebugAntilog())
}