package com.arcadan.dodgetheenemies.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.enums.Coin
import com.arcadan.dodgetheenemies.enums.Gem
import com.arcadan.dodgetheenemies.enums.Heart
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.enums.SkinEnum
import com.arcadan.dodgetheenemies.fragments.ShopFragmentDirections
import com.arcadan.dodgetheenemies.models.Shop
import com.google.android.material.snackbar.Snackbar

class ShopViewModel : ViewModel() {
    private var shopModels: MutableList<Shop> = mutableListOf()
    private var _items = MutableLiveData<List<Shop>>()
    val items: LiveData<List<Shop>>
        get() = _items

    init {
        showPowerUp()
    }

    fun showPowerUp() {
        shopModels.clear()
        for (enum in PowerUp.values()) {
            shopModels.add(Shop(enum.name, enum.topText, enum.image, enum.iconImage, enum.bottomText, enum.price, enum.unit, enum.reward, enum.rewardType))
        }
        _items.value = shopModels
    }

    fun showHeartsItems() {
        shopModels.clear()
        for (enum in Heart.values()) {
            shopModels.add(Shop(enum.name, enum.topText, enum.image, enum.iconImage, enum.bottomText, enum.price, enum.unit, enum.reward, enum.rewardType))
        }
        _items.value = shopModels
    }

    fun showCoinsItems() {
        shopModels.clear()
        for (enum in Coin.values()) {
            shopModels.add(Shop(enum.name, enum.topText, enum.image, enum.iconImage, enum.bottomText, enum.price, enum.unit, enum.reward, enum.rewardType))
        }
        _items.value = shopModels
    }

    fun showGemItems() {
        shopModels.clear()
        for (enum in Gem.values()) {
            shopModels.add(Shop(enum.name, enum.topText, enum.image, enum.iconImage, enum.bottomText, enum.price, enum.unit, enum.reward, enum.rewardType, enum.sku))
        }
        _items.value = shopModels
    }

    fun showDodgersSkinItems(view: View) {
        Snackbar.make(view, Dodgers.context!!.getText(R.string.coming_soon), Snackbar.LENGTH_SHORT).show()
        shopModels.clear()
        for (enum in SkinEnum.values()) {
            if (enum.id != 0)
                shopModels.add(Shop(enum.name, enum.topText, enum.image, enum.iconImage, enum.bottomText, enum.price, enum.unit))
        }
        _items.value = shopModels
    }

    fun goBack(view: View) {
        view.findNavController().navigate(ShopFragmentDirections.actionShopFragmentToMainFragment())
    }
}
