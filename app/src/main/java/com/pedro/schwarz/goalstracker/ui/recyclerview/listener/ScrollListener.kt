package com.pedro.schwarz.goalstracker.ui.recyclerview.listener

import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.ui.constants.SCROLL_DOWN
import com.pedro.schwarz.goalstracker.ui.constants.SCROLL_UP

class ScrollListener(private val onScroll: (scrollDirection: Int) -> Unit) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            onScroll(SCROLL_DOWN)
        } else {
            onScroll(SCROLL_UP)
        }
    }
}