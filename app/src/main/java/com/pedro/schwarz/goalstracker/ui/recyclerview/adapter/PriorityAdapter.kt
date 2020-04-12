package com.pedro.schwarz.goalstracker.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.databinding.ItemPriorityBinding
import com.pedro.schwarz.goalstracker.model.Priority

class PriorityAdapter(var onItemClick: (priority: Priority) -> Unit = {}) :
    ListAdapter<Priority, PriorityAdapter.ViewHolder>(PriorityDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = ItemPriorityBinding.inflate(inflater, parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val viewBinding: ItemPriorityBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        private lateinit var priority: Priority

        init {
            viewBinding.onItemClick = View.OnClickListener {
                onItemClick(priority)
            }
        }

        fun bind(item: Priority) {
            this.priority = item
            setContent()
        }

        private fun setContent() {
            if (::priority.isInitialized) {
                viewBinding.priority = priority
            }
        }
    }
}

object PriorityDiffCallback : DiffUtil.ItemCallback<Priority>() {
    override fun areItemsTheSame(oldItem: Priority, newItem: Priority): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Priority, newItem: Priority): Boolean =
        oldItem == newItem
}