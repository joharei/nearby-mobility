package app.reitan.common.di

import android.util.Log
import app.reitan.common.Repository
import app.reitan.common.entur.EnturApi
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean = true, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule(enableNetworkLogs = enableNetworkLogs))
    }

fun commonModule(enableNetworkLogs: Boolean) = module {
    single { createJson() }
    single { createHttpClient(get(), enableNetworkLogs = enableNetworkLogs) }
    single { Repository(get()) }
    single { EnturApi(get()) }
}

fun createJson() = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

fun createHttpClient(json: Json, enableNetworkLogs: Boolean) = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
    if (enableNetworkLogs) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", message)
                }
            }
            level = LogLevel.BODY
        }
    }
}
