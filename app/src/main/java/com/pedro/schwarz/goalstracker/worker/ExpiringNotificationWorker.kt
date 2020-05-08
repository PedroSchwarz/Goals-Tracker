package com.pedro.schwarz.goalstracker.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pedro.schwarz.goalstracker.repository.GoalRepository
import com.pedro.schwarz.goalstracker.service.showNotification
import org.koin.core.KoinComponent
import org.koin.core.inject

class ExpiringNotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params),
    KoinComponent {

    override fun doWork(): Result {
        val goalRepository by inject<GoalRepository>()
        goalRepository.fetchExpiringUncompletedGoals(onCompleted = { goals ->
            if (goals.isNotEmpty()) {
                goals.forEach { goal ->
                    showNotification(
                        applicationContext,
                        "Goal Expiring",
                        "Warning about ${goal.title}",
                        "Your goal ${goal.title} is about reach it's target date and you haven't completed it yet!"
                    )
                }
            }
        })
        return Result.success()
    }
}