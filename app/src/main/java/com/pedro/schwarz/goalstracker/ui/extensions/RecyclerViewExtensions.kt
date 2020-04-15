package com.pedro.schwarz.goalstracker.ui.extensions

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

fun <T> RecyclerView.setContent(
    direction: Int,
    itemAdapter: T
) {
    setHasFixedSize(true)
    layoutManager = LinearLayoutManager(this.context, direction, false)
    adapter = itemAdapter as RecyclerView.Adapter<*>
}