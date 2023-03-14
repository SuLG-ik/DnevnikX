package ru.sulgik.about.domain.data

data class AboutOutput(
    val application: ApplicationData,
    val domain: DomainInfo,
    val developer: DeveloperData,
) {
    class ApplicationData(
        val name: String,
        val version: String,
        val fullName: String,
    )

    class DomainInfo(
        val name: String,
        val domain: String,
        val uri: String,
    )

    class DeveloperData(
        val name: String,
        val uri: String,
    )
}