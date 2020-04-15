package com.pedro.schwarz.goalstracker.ui.databinding.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.Icon
import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pedro.schwarz.goalstracker.R
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

@BindingAdapter("loadStateIcon")
fun FloatingActionButton.loadStateIcon(state: Boolean) {
    val icon = if (state) R.drawable.ic_done
    else R.drawable.ic_undone
    val color = if (state) R.color.colorSuccess
    else R.color.colorError
    supportImageTintList = ColorStateList.valueOf(resources.getColor(color, null))
    setImageIcon(Icon.createWithResource(this.context, icon))
}

@BindingAdapter("hide")
fun FloatingActionButton.loadOptionState(hide: Boolean) {
    if (hide) {
        hide()
    } else {
        show()
    }
}

@BindingAdapter("scrollDown", "hide")
fun FloatingActionButton.loadMenuState(scrollDown: Boolean, hide: Boolean) {
    val menuAnimation = if (hide) R.anim.rotate_backward
    else R.anim.rotate_forward
    animation = AnimationUtils.loadAnimation(this.context, menuAnimation)
    if (scrollDown) {
        hide()
    } else {
        show()
    }
}