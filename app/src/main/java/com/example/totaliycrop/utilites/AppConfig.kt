package com.example.totaliycrop.utilites

import android.app.Application
import com.example.totaliycrop.di.repositoryModule
import com.example.totaliycrop.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
@Suppress("unused")
class AppConfig:Application()  {
    override fun onCreate( ) {
        super.onCreate()
        startKoin {
            androidContext(this@AppConfig)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}