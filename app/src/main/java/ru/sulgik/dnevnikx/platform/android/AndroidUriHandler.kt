package ru.sulgik.dnevnikx.platform.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import ru.sulgik.dnevnikx.platform.UriHandler

class AndroidUriHandler(
    private val context: Context
): UriHandler {

    override fun open(uri: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

}