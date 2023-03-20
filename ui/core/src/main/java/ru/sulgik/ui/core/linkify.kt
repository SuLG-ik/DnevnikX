package ru.sulgik.ui.core

import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.util.LinkifyCompat

@Composable
fun String.linkify(
    linkStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
    ),
) = remember(this, linkStyle) {
    buildAnnotatedString {
        append(this@linkify)

        val spannable = SpannableString(this@linkify)
        LinkifyCompat.addLinks(spannable, Linkify.WEB_URLS)

        val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
        for (span in spans) {
            val start = spannable.getSpanStart(span)
            val end = spannable.getSpanEnd(span)

            addStyle(
                start = start,
                end = end,
                style = linkStyle,
            )
            addStringAnnotation(
                tag = "URL",
                annotation = span.url,
                start = start,
                end = end
            )
        }
    }
}

fun AnnotatedString.urlAt(position: Int): String? =
    getStringAnnotations("URL", position, position).firstOrNull()?.item