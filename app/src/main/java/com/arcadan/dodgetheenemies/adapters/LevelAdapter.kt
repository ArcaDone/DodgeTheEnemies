package com.arcadan.dodgetheenemies.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arcadan.dodgetheenemies.databinding.ItemLevelBinding
import com.arcadan.dodgetheenemies.models.Level

class LevelAdapter(private val onLevelClickListener: OnLevelClickListener) :
    ListAdapter<Level, LevelAdapter.LevelViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holderLevel: LevelViewHolder, position: Int) {
        val item = getItem(position)
        holderLevel.bind(item)
        holderLevel.itemView.setOnClickListener {
            onLevelClickListener.onClick(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        return LevelViewHolder(
            ItemLevelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class LevelViewHolder(private val binding: ItemLevelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Level) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    class OnLevelClickListener(val clickListener: (level: Level) -> Unit) {
        fun onClick(level: Level) = clickListener(level)
    }

    class DiffCallback : DiffUtil.ItemCallback<Level>() {
        override fun areItemsTheSame(oldItem: Level, newItem: Level): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Level, newItem: Level): Boolean {
            return oldItem == newItem
        }
    }
}
