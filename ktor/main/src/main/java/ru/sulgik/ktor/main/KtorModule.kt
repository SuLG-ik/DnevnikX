package ru.sulgik.koin.main

import android.util.Log
import io.github.aakira.napier.BuildConfig
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toMap
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.sulgik.common.CustomLocalDateSerializer

class KtorModule {


    private fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Resources)
            defaultRequest {
                contentType(ContentType.Application.Json)
                this.url {
                    protocol = URLProtocol.HTTPS
                    host = "school.nso.ru"
                    path("apiv3/")
                    parameters.append("vendor", "school")
                    parameters.append("devkey", "0c7968cd2b6e14a4eed3c94e593ae9f0")
                    parameters.append("out_format", "json")
                    Log.d(
                        "pisus",
                        "parameters: ${
                            parameters.build().toMap().map { it.value.joinToString() }
                                .joinToString()
                        }"
                    )
                }
            }
            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Napier.d(message, tag = "UnauthorizedHttpClient")
                        }
                    }
                    level = LogLevel.ALL
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                    serializersModule = SerializersModule {
                        contextual(CustomLocalDateSerializer)
                    }
                    isLenient = true
                })
            }
        }
    }

    val module = module {
        singleOf(::provideHttpClient)
    }

}