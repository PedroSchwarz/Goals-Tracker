package com.pedro.schwarz.goalstracker.data

import android.content.Context
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.model.Priority

fun getPriorities(context: Context): List<Priority> {
    return listOf(
        Priority(
            id = 1,
            icon = R.drawable.ic_low,
            color = R.color.colorLow,
            title = "Low"
        ),
        Priority(
            id = 2,
            icon = R.drawable.ic_medium,
            color = R.color.colorMedium,
            title = "Medium"
        ),
        Priority(
            id = 3,
            icon = R.drawable.ic_high,
            color = R.color.colorHigh,
            title = "High"
        )
    )
}