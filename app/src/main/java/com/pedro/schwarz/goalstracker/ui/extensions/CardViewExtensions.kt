package com.pedro.schwarz.goalstracker.ui.extensions

import androidx.cardview.widget.CardView

fun CardView.loadCardColor(color: Int) {
    setCardBackgroundColor(resources.getColor(color, null))
}