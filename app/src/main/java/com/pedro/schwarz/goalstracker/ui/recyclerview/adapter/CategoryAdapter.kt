package com.pedro.schwarz.goalstracker.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.databinding.ItemCategoryBinding
import com.pedro.schwarz.goalstracker.model.Category

class CategoryAdapter(var onItemClick: (category: Category) -> Unit = {}) :
    ListAdapter<Category, CategoryAdapter.ViewHolder>(CategoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = ItemCategoryBinding.inflate(inflater, parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val viewBinding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        private lateinit var category: Category

        init {
            viewBinding.onItemClick = View.OnClickListener {
                onItemClick(category)
            }
        }

        fun bind(item: Category) {
            this.category = item
            setContent()
        }

        private fun setContent() {
            if (::category.isInitialized) {
                viewBinding.category = category
            }
        }
    }
}

object CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
        oldItem == newItem
}