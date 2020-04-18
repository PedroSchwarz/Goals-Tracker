package com.pedro.schwarz.goalstracker.application

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pedro.schwarz.goalstracker.di.modules.daoModules
import com.pedro.schwarz.goalstracker.di.modules.databaseModules
import com.pedro.schwarz.goalstracker.di.modules.uiModules
import com.pedro.schwarz.goalstracker.di.modules.viewModelModules
import com.pedro.schwarz.goalstracker.worker.NotificationWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppApplication)
            modules(listOf(databaseModules, daoModules, uiModules, viewModelModules))
        }
        checkExpiringGoals()
    }

    private fun checkExpiringGoals() {
        val request = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueue(request)
    }
}