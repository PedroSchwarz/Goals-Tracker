package com.pedro.schwarz.goalstracker.application

import android.app.Application
import com.pedro.schwarz.goalstracker.di.modules.daoModules
import com.pedro.schwarz.goalstracker.di.modules.databaseModules
import com.pedro.schwarz.goalstracker.di.modules.uiModules
import com.pedro.schwarz.goalstracker.di.modules.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppApplication)
            modules(listOf(databaseModules, daoModules, uiModules, viewModelModules))
        }
    }
}