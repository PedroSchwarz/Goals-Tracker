package com.pedro.schwarz.goalstracker.ui.databinding

import android.content.res.ColorStateList
import android.graphics.drawable.Icon
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pedro.schwarz.goalstracker.data.getCategories
import com.pedro.schwarz.goalstracker.data.getPriorities

@BindingAdapter("loadCategoryContent")
fun FloatingActionButton.loadCategoryContent(id: Long) {
    val category = getCategories().find { category -> category.id == id }
    category?.let {
        supportBackgroundTintList = ColorStateList.valueOf(resources.getColor(category.color, null))
        setImageIcon(Icon.createWithResource(this.context, category.icon))
    }
}

@BindingAdapter("loadPriorityContent")
fun FloatingActionButton.loadPriorityContent(id: Long) {
    val priority = getPriorities().find { priority -> priority.id == id }
    priority?.let {
        supportBackgroundTintList = ColorStateList.valueOf(resources.getColor(priority.color, null))
        setImageIcon(Icon.createWithResource(this.context, priority.icon))
    }
}