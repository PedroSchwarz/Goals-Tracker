package com.pedro.schwarz.goalstracker.ui.databinding.adapter

import android.content.res.ColorStateList
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.pedro.schwarz.goalstracker.data.getCategories
import com.pedro.schwarz.goalstracker.data.getPriorities

@BindingAdapter("milestones", "completedMilestones")
fun Chip.setProgress(milestones: Int, completedMilestones: Int) {
    text = "$completedMilestones/$milestones"
}

@BindingAdapter("loadCategoryContent")
fun Chip.loadCategoryContent(id: Long) {
    val category = getCategories().find { category -> category.id == id }
    category?.let {
        text = category.title
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(category.color, null))
        chipIcon = resources.getDrawable(category.icon, null)
    }
}

@BindingAdapter("loadPriorityContent")
fun Chip.loadPriorityContent(id: Long) {
    val priority = getPriorities().find { priority -> priority.id == id }
    priority?.let {
        text = priority.title
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(priority.color, null))
        chipIcon = resources.getDrawable(priority.icon, null)
    }
}