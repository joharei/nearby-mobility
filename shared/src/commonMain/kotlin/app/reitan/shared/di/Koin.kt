package app.reitan.shared.di

import app.reitan.shared.Repository
import app.reitan.shared.entur.EnturApi
import app.reitan.shared.ryde.RydeApi
import io.ktor.client.*
import io.ktor.client.plugins.*
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

// Called by iOS etc
@Suppress("unused")
fun initKoin() = initKoin(enableNetworkLogs = false)

private fun commonModule(enableNetworkLogs: Boolean) = module {
    single { createJson() }
    single { createHttpClient(get(), enableNetworkLogs = enableNetworkLogs) }
    single { Repository() }
    single { RydeApi(get()) }
    single { EnturApi(get()) }
}

private fun createJson() = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

private fun createHttpClient(json: Json, enableNetworkLogs: Boolean) = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
    if (enableNetworkLogs) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
