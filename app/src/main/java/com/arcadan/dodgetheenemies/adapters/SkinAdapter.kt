package com.arcadan.dodgetheenemies.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.databinding.ItemSkinBinding
import com.arcadan.dodgetheenemies.models.Skin
import kotlinx.android.synthetic.main.item_skin.view.imageViewFrame1
import kotlinx.android.synthetic.main.item_skin.view.imageViewFrame2

class SkinAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Skin, SkinAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        val item = getItem(position)
        val selectedSkin = Persistence.instance.getInt(Keys.SELECTED_SKIN)
        if (position == selectedSkin) {
            holderItem.itemView.imageViewFrame1.visibility = View.VISIBLE
            holderItem.itemView.imageViewFrame2.visibility = View.VISIBLE
        } else {
            holderItem.itemView.imageViewFrame1.visibility = View.INVISIBLE
            holderItem.itemView.imageViewFrame2.visibility = View.INVISIBLE
        }

        holderItem.itemView.setOnClickListener {
            onClickListener.onClick(item)
            notifyDataSetChanged()
        }
        holderItem.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemSkinBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ItemViewHolder(private val binding: ItemSkinBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Skin) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (item: Skin) -> Unit) {
        fun onClick(item: Skin) = clickListener(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<Skin>() {
        override fun areItemsTheSame(oldItem: Skin, newItem: Skin): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Skin, newItem: Skin): Boolean {
            return oldItem == newItem
        }
    }
}
