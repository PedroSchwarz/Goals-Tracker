package com.pedro.schwarz.goalstracker.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.service.showNotification

private const val GOAL_TITLE_KEY = "goalTitle"

class CompletingNotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString(GOAL_TITLE_KEY)
        showNotification(
            applicationContext,
            applicationContext.getString(R.string.goal_almost_complete_title),
            applicationContext.getString(R.string.goal_almost_complete_description),
            applicationContext.getString(R.string.goal_almost_complete_long_description_start) + title + applicationContext.getString(
                R.string.goal_almost_complete_long_description_end
            )
        )

        return Result.success()
    }
}