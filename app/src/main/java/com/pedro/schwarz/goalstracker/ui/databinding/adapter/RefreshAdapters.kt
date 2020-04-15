package com.pedro.schwarz.goalstracker.ui.databinding.adapter

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("setIsRefreshing")
fun SwipeRefreshLayout.setIsRefreshing(isRefreshing: Boolean) {
    this.isRefreshing = isRefreshing
}