package com.pedro.schwarz.goalstracker.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.databinding.ItemCheckpointBinding
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.ui.databinding.CheckpointData

private const val SET_LIFECYCLE = 1
private const val UNSET_LIFECYCLE = 2

class CheckpointAdapter(var onItemClick: (checkpoint: Checkpoint) -> Unit = {}) :
    ListAdapter<Checkpoint, CheckpointAdapter.ViewHolder>(CheckpointDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = ItemCheckpointBinding.inflate(inflater, parent, false)
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

    inner class ViewHolder(private val viewBinding: ItemCheckpointBinding) :
        RecyclerView.ViewHolder(viewBinding.root), LifecycleOwner {

        private val registry: LifecycleRegistry = LifecycleRegistry(this)

        private lateinit var checkpoint: Checkpoint

        init {
            registry.currentState = Lifecycle.State.INITIALIZED
            viewBinding.onItemClick = View.OnClickListener {
                onItemClick(checkpoint)
            }
        }

        fun bind(item: Checkpoint) {
            this.checkpoint = item
            setContent()
        }

        private fun setContent() {
            if (::checkpoint.isInitialized) {
                viewBinding.checkpoint = CheckpointData(checkpoint = checkpoint)
            }
        }

        fun changeLifeCycleState(state: Int) {
            when (state) {
                SET_LIFECYCLE -> {
                    registry.currentState = Lifecycle.State.STARTED
                }
                else -> {
                    registry.currentState = Lifecycle.State.DESTROYED
                }
            }
        }

        override fun getLifecycle(): Lifecycle = registry
    }
}

object CheckpointDiffCallback : DiffUtil.ItemCallback<Checkpoint>() {
    override fun areItemsTheSame(oldItem: Checkpoint, newItem: Checkpoint): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Checkpoint, newItem: Checkpoint): Boolean =
        oldItem == newItem
}