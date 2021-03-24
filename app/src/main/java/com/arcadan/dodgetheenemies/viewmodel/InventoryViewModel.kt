package com.arcadan.dodgetheenemies.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.enums.Consumables
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.fragments.InventoryFragmentDirections
import com.arcadan.dodgetheenemies.models.Consumable

class InventoryViewModel : ViewModel() {
    private var consumableModels: MutableList<Consumable>? = null

    private var _listItems = MutableLiveData<List<Consumable>>()
    val listItems: LiveData<List<Consumable>>
        get() = _listItems

    var selectedItem = MutableLiveData(
        Consumable(
            Consumables.SLURP_RED, 0
        )
    )

    init {
        fetchInventoryItems()
    }

    private fun fetchInventoryItems() {
        consumableModels = ArrayList()

        val user = DataManager.instance.user.value
        require(user != null) { "User | user | null exception :- ( . ${DataManager.instance.user}" }

        consumableModels!!.addAll(
            listOf(
                Consumable(
                    Consumables.SLURP_RED,
                    PowerUp.SLURP_RED.ordinal.let { user.consumables[it].quantity }
                ),
                Consumable(
                    Consumables.SPEED_UP,
                    PowerUp.SPEED_UP.ordinal.let { user.consumables[it].quantity }
                ),
                Consumable(
                    Consumables.MEGA_JUMP,
                    PowerUp.MEGA_JUMP.ordinal.let { user.consumables[it].quantity }
                ),
                Consumable(
                    Consumables.DOUBLE_COINS,
                    PowerUp.DOUBLE_COINS.ordinal.let { user.consumables[it].quantity }
                ),
                Consumable(Consumables.SOON, 0)
            )
        )

        _listItems.value = consumableModels
    }

    fun goBack(view: View) {
        view.findNavController()
            .navigate(InventoryFragmentDirections.actionInventoryFragmentToMainFragment())
    }
}
