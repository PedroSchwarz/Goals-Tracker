package com.pedro.schwarz.goalstracker.ui.databinding.adapter

import android.content.res.ColorStateList
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.ui.extensions.loadCardColor

@BindingAdapter("loadCardColor")
fun CardView.loadCardColor(color: Int?) {
    color?.let {
        loadCardColor(color)
    }
}

@BindingAdapter("loadStateColor")
fun CardView.loadStateColor(state: Boolean) {
    val color = if (state) R.color.colorPrimaryDark
    else R.color.colorPrimary
    setCardBackgroundColor(ColorStateList.valueOf(resources.getColor(color, null)))
}