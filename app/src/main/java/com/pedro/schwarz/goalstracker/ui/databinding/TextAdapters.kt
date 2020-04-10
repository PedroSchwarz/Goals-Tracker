package com.pedro.schwarz.goalstracker.ui.databinding

import android.text.format.DateUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("formatDate")
fun TextView.formatDate(date: Long) {
    text = DateUtils.getRelativeTimeSpanString(date)
}