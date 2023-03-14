package ru.sulgik.about.domain

import android.content.Context
import ru.sulgik.about.domain.data.AboutOutput
import ru.sulgik.about.domain.data.BuiltInAboutRepository

class BuildConfigBuiltInAboutRepository(
    context: Context,
) : BuiltInAboutRepository {

    val data = AboutOutput(
        application = AboutOutput.ApplicationData(
            name = "DnevnikX",
            version = context.getString(ru.sulgik.about.domain.builtin.R.string.app_version),
            fullName = "DnevnikX ${context.getString(ru.sulgik.about.domain.builtin.R.string.app_version)}"
        ),
        developer = AboutOutput.DeveloperData(
            name = "@vollllodya",
            uri = "https://t.me/vollllodya",
        ),
        domain = AboutOutput.DomainInfo(
            name = "Новосибирская область",
            domain = "school.nso.ru",
            uri = "https://school.nso.ru"
        )
    )

    override fun getAboutData(): AboutOutput {
        return data
    }

}