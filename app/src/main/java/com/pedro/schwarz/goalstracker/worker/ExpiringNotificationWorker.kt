package com.pedro.schwarz.goalstracker.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pedro.schwarz.goalstracker.R
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
                        applicationContext.getString(R.string.goal_expiring_title),
                        applicationContext.getString(R.string.goal_expiring_description) + goal.title,
                        applicationContext.getString(R.string.goal_expiring_long_description_start) + goal.title + applicationContext.getString(
                            R.string.goal_expiring_long_description_end
                        )
                    )
                }
            }
        })
        return Result.success()
    }
}