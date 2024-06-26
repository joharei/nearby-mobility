package app.reitan.nearby_mobility

import android.app.Application
import app.reitan.common.di.initKoin
import app.reitan.nearby_mobility.di.appModule
import org.koin.android.ext.koin.androidContext

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@Application)
            modules(appModule)
        }
    }
}
