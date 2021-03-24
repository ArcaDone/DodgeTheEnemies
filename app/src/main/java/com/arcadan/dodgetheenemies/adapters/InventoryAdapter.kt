package com.arcadan.dodgetheenemies.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arcadan.dodgetheenemies.databinding.ItemConsumableBinding
import com.arcadan.dodgetheenemies.models.Consumable

class InventoryAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Consumable, InventoryAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holderItem.itemView.setOnClickListener { onClickListener.onClick(item) }
        holderItem.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemConsumableBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ItemViewHolder(private val binding: ItemConsumableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Consumable) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (item: Consumable) -> Unit) {
        fun onClick(item: Consumable) = clickListener(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<Consumable>() {
        override fun areItemsTheSame(oldItem: Consumable, newItem: Consumable): Boolean {
            return oldItem.consumables.title == newItem.consumables.title
        }

        override fun areContentsTheSame(oldItem: Consumable, newItem: Consumable): Boolean {
            return oldItem == newItem
        }
    }
}
