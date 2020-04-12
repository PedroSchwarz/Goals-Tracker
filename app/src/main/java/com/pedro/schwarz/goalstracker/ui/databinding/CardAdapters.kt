package com.pedro.schwarz.goalstracker.ui.databinding

import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.pedro.schwarz.goalstracker.ui.extensions.loadCardColor

@BindingAdapter("loadCardColor")
fun CardView.loadCardColor(color: Int?) {
    color?.let {
        loadCardColor(color)
    }
}