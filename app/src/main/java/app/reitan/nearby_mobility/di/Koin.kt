package app.reitan.nearby_mobility.di

import app.reitan.nearby_mobility.features.map.ScooterMapViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    viewModel { ScooterMapViewModel(get()) }
}