package ru.sulgik.about.domain.data

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource

interface BuiltInAboutRepository {

    fun getAboutData(authScope: AuthScope): FlowResource<AboutOutput>

    fun getApplicationData(): AboutOutput.ApplicationData

}