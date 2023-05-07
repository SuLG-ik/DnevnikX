package ru.sulgik.ktor.main

import io.github.aakira.napier.BuildConfig
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
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
                    path("apiv3/")
                    parameters.append("out_format", "json")
                }
            }
            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Napier.d(message, tag = "HttpClient")
                        }
                    }
                    level = LogLevel.ALL
                }
            }
            install(HttpRequestRetry) {
                retryOnException(maxRetries = 10)
                exponentialDelay(base = 1.5, respectRetryAfterHeader = false)
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