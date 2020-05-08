package com.pedro.schwarz.goalstracker.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pedro.schwarz.goalstracker.service.showNotification

private const val GOAL_TITLE_KEY = "goalTitle"

class CompletingNotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString(GOAL_TITLE_KEY)
        showNotification(
            applicationContext,
            "Goal Expiring",
            "Congrats!",
            "Your goal $title is almost complete! Keep it up"
        )

        return Result.success()
    }
}