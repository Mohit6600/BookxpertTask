package com.example.bookxpert.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookxpert.data.local.model.LocalApiItem
import com.example.bookxpert.databinding.ItemApiBinding

class ApiItemAdapter(
    private val onItemClick: (LocalApiItem) -> Unit,
    private val onDeleteClick: (LocalApiItem) -> Unit,
    private val onUpdateClick: (LocalApiItem) -> Unit
) : ListAdapter<LocalApiItem, ApiItemAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemApiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            textName.text = item.name
            textId.text = item.id
            textPrice.text = item.color?.toString() ?: "N/A"
            textCpu.text = item.capacity ?: "N/A"

            root.setOnClickListener { onItemClick(item) }
            buttonDelete.setOnClickListener { onDeleteClick(item) }
            buttonEdit.setOnClickListener { onUpdateClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LocalApiItem>() {
        override fun areItemsTheSame(oldItem: LocalApiItem, newItem: LocalApiItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocalApiItem, newItem: LocalApiItem): Boolean {
            return oldItem == newItem
        }
    }
}