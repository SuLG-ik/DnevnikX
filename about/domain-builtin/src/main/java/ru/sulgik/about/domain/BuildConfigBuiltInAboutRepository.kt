package ru.sulgik.about.domain

import android.content.Context
import ru.sulgik.about.domain.data.AboutOutput
import ru.sulgik.about.domain.data.BuiltInAboutRepository
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.domain.MergedAuthRepository
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger

class BuildConfigBuiltInAboutRepository(
    context: Context,
    private val authRepository: MergedAuthRepository,
) : BuiltInAboutRepository {

    private val applicationData = AboutOutput.ApplicationData(
        name = "DnevnikX",
        version = context.getString(ru.sulgik.about.domain.builtin.R.string.app_version),
        fullName = "DnevnikX ${context.getString(ru.sulgik.about.domain.builtin.R.string.app_version)}"
    )

    private val developerData = AboutOutput.DeveloperData(
        name = "@vollllodya",
        uri = "https://t.me/vollllodya",
    )

    private val merger = Merger.named("About")

    override fun getAboutData(authScope: AuthScope): FlowResource<AboutOutput> {
        return merger.local(localRequest = {
            val authorization = authRepository.getAuthorization(authScope.id)
            AboutOutput(
                application = getApplicationData(),
                developer = developerData,
                domain = AboutOutput.DomainInfo(
                    name = authorization.vendor.realName,
                    domain = authorization.vendor.host,
                    uri = "https://${authorization.vendor.host}",
                    logo = authorization.vendor.logo,
                ),
            )
        })
    }

    override fun getApplicationData(): AboutOutput.ApplicationData {
        return applicationData
    }
}