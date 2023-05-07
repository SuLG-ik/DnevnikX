package ru.sulgik.auth.domain.data

import ru.sulgik.images.RemoteImage


class AuthorizationWithoutVendor(
    val token: String,
    val accountId: String,
)

class Authorization(
    val token: String,
    val accountId: String,
    val vendor: Vendor,
)

class Vendor(
    val region: String,
    val realName: String,
    val vendor: String,
    val host: String,
    val devKey: String,
    val logo: RemoteImage,
)