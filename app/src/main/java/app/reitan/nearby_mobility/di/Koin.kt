package app.reitan.nearby_mobility.di

import app.reitan.nearby_mobility.AppViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    viewModel { AppViewModel(get()) }
}