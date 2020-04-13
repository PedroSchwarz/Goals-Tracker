package com.pedro.schwarz.goalstracker.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.databinding.ItemMilestoneBinding
import com.pedro.schwarz.goalstracker.model.Milestone
import com.pedro.schwarz.goalstracker.ui.databinding.MilestoneData

private const val SET_LIFECYCLE = 1
private const val UNSET_LIFECYCLE = 2

class MilestoneAdapter(var onItemClick: (milestone: Milestone, toggle: Boolean) -> Unit = { _, _ -> }) :
    ListAdapter<Milestone, MilestoneAdapter.ViewHolder>(MilestoneDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = ItemMilestoneBinding.inflate(inflater, parent, false)
        return ViewHolder(viewBinding).also { holder ->
            viewBinding.lifecycleOwner = holder
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.changeLifeCycleState(SET_LIFECYCLE)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.changeLifeCycleState(UNSET_LIFECYCLE)
    }

    inner class ViewHolder(private val viewBinding: ItemMilestoneBinding) :
        RecyclerView.ViewHolder(viewBinding.root), LifecycleOwner {

        private val registry: LifecycleRegistry = LifecycleRegistry(this)

        private lateinit var milestone: Milestone

        init {
            registry.currentState = State.INITIALIZED
            viewBinding.onItemClick = View.OnClickListener {
                onItemClick(milestone, false)
            }
            viewBinding.onCheck = View.OnClickListener {
                onItemClick(milestone, true)
            }
        }

        fun bind(item: Milestone) {
            this.milestone = item
            setContent()
        }

        private fun setContent() {
            if (::milestone.isInitialized) {
                viewBinding.milestone = MilestoneData(milestone = milestone)
            }
        }

        fun changeLifeCycleState(state: Int) {
            when (state) {
                SET_LIFECYCLE -> {
                    registry.currentState = State.STARTED
                }
                else -> {
                    registry.currentState = State.DESTROYED
                }
            }
        }

        override fun getLifecycle(): Lifecycle = registry
    }
}

object MilestoneDiffCallback : DiffUtil.ItemCallback<Milestone>() {
    override fun areItemsTheSame(oldItem: Milestone, newItem: Milestone): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Milestone, newItem: Milestone): Boolean =
        oldItem == newItem
}