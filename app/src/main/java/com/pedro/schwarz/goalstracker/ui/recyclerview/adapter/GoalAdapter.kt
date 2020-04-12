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
import com.pedro.schwarz.goalstracker.data.getCategories
import com.pedro.schwarz.goalstracker.data.getPriorities
import com.pedro.schwarz.goalstracker.databinding.ItemGoalBinding
import com.pedro.schwarz.goalstracker.model.Category
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.model.Priority
import com.pedro.schwarz.goalstracker.ui.databinding.GoalData

private const val SET_LIFECYCLE = 1
private const val UNSET_LIFECYCLE = 2

class GoalAdapter(var onItemClick: (goal: Goal) -> Unit = {}) :
    ListAdapter<Goal, GoalAdapter.ViewHolder>(GoalDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = ItemGoalBinding.inflate(inflater, parent, false)
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

    inner class ViewHolder(private val viewBinding: ItemGoalBinding) :
        RecyclerView.ViewHolder(viewBinding.root), LifecycleOwner {

        private val registry = LifecycleRegistry(this)

        private lateinit var goal: Goal

        init {
            registry.currentState = State.INITIALIZED
            viewBinding.onItemClick = View.OnClickListener {
                onItemClick(goal)
            }
        }

        fun bind(goal: Goal) {
            this.goal = goal
            viewBinding.goal = GoalData(goal = goal)
            getCategory(goal)?.let {
                viewBinding.category = it
            }
            getPriority(goal)?.let {
                viewBinding.priority = it
            }
        }

        private fun getCategory(goal: Goal): Category? =
            getCategories().find { category -> category.id == goal.categoryId }

        private fun getPriority(goal: Goal): Priority? =
            getPriorities().find { priority -> priority.id == goal.priorityId }

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

object GoalDiffCallback : DiffUtil.ItemCallback<Goal>() {
    override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean = oldItem == newItem
}