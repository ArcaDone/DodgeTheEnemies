package com.arcadan.dodgetheenemies.util

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.adapters.InventoryAdapter
import com.arcadan.dodgetheenemies.adapters.LevelAdapter
import com.arcadan.dodgetheenemies.adapters.ShopAdapter
import com.arcadan.dodgetheenemies.adapters.SkinAdapter
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.models.Consumable
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.models.Shop
import com.arcadan.dodgetheenemies.models.Skin
import com.bumptech.glide.Glide

@BindingAdapter("inventoryListData")
fun bindInventoryRecyclerView(recyclerView: RecyclerView, data: List<Consumable>) {
    val adapter = recyclerView.adapter as InventoryAdapter
    adapter.submitList(data)
}

@BindingAdapter("shopListData")
fun bindShopListRecyclerView(recyclerView: RecyclerView, data: List<Shop>) {
    val adapter = recyclerView.adapter as ShopAdapter
    adapter.submitList(data)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("skinListData")
fun bindSkinListRecyclerView(recyclerView: RecyclerView, data: List<Skin>) {
    val adapter = recyclerView.adapter as SkinAdapter
    adapter.submitList(data)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("levelListData")
fun bindLevelsRecyclerView(recyclerView: RecyclerView, data: List<Level>) {
    val adapter = recyclerView.adapter as LevelAdapter
    adapter.submitList(data)
}

@BindingAdapter("consumableSizeImage")
fun bindConsumableSizeImage(imageView: ImageView, size: Int) {
    when (size) {
        0 -> imageView.setImageResource(R.drawable.ic_plus_zero)
        1 -> imageView.setImageResource(R.drawable.ic_plus_one)
        2 -> imageView.setImageResource(R.drawable.ic_plus_two)
        3 -> imageView.setImageResource(R.drawable.ic_plus_three)
        4 -> imageView.setImageResource(R.drawable.ic_plus_four)
        5 -> imageView.setImageResource(R.drawable.ic_plus_five)
        6 -> imageView.setImageResource(R.drawable.ic_plus_six)
        7 -> imageView.setImageResource(R.drawable.ic_plus_seven)
        8 -> imageView.setImageResource(R.drawable.ic_plus_eight)
        9 -> imageView.setImageResource(R.drawable.ic_plus_nine)
        else -> imageView.setImageResource(R.drawable.ic_plus_ten)
    }
}

@BindingAdapter("getLevelImage")
fun bindGetLevelImage(imageView: ImageView, image: Int) {
    Glide.with(Dodgers.context!!)
        .load(image)
        .override(990, 450)
        .centerCrop()
        .into(imageView)
}

@BindingAdapter("coinRewards")
fun bindRewardCoins(textView: TextView, image: Int) {
    textView.text = image.toString()
}

@BindingAdapter("getImage")
fun bindGetImage(imageView: ImageView, image: Int) {
        Glide.with(Dodgers.context!!)
            .load(image)
            .into(imageView)
}

@BindingAdapter("getBackgroundImage")
fun bindGetBackgroundImage(view: View, image: Int) {
    val background = when (image) {
        R.drawable.power_up_slurp_red -> R.color.joystickLightBlue
        R.drawable.power_up_speed_up -> R.color.joystickPink
        R.drawable.power_up_mega_jump -> R.color.joystickYellow
        R.drawable.power_up_double_coins -> R.color.joystickGreen
        else -> R.color.joystickLightBlue
    }
    view.backgroundTintList = ContextCompat.getColorStateList(Dodgers.context!!, background)
}

@BindingAdapter("levelVisibility")
fun bindLevelVisibility(relative: RelativeLayout, levelId: Int) {
    if (levelId > DataManager.instance.user.value!!.unlockedLevel) relative.visibility =
        View.VISIBLE else relative.visibility = View.INVISIBLE
}
