package com.pedro.schwarz.goalstracker.ui.databinding

import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("milestones", "completedMilestones")
fun ProgressBar.setProgress(milestones: Int, completedMilestones: Int) {
    progress = if (milestones > 0) (completedMilestones * 100 / milestones)
    else 0
}