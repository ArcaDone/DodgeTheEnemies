package com.arcadan.dodgetheenemies.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arcadan.dodgetheenemies.databinding.ItemShopBinding
import com.arcadan.dodgetheenemies.models.Shop

class ShopAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Shop, ShopAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holderItem.itemView.setOnClickListener { onClickListener.onClick(item) }
        holderItem.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ItemViewHolder(private val binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Shop) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (item: Shop) -> Unit) {
        fun onClick(item: Shop) = clickListener(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<Shop>() {
        override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return oldItem.topText == newItem.topText
        }

        override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return oldItem == newItem
        }
    }
}
