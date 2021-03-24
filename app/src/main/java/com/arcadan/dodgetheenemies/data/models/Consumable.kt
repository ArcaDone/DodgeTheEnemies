package com.arcadan.dodgetheenemies.data.models

import com.arcadan.dodgetheenemies.data.DatabaseManager
import com.arcadan.dodgetheenemies.enums.PowerUp

class Consumable(
    val name: String,
    quantity: Int
) {
    private var _quantity = quantity
    var quantity: Int
        get() = _quantity
        set(value) {
            _quantity = value
            DatabaseManager.setValue("consumables/${PowerUp.valueOf(name).ordinal}/quantity", _quantity)
        }
    companion object {
        fun fromHashMap(map: HashMap<*, *>): Consumable {
            return Consumable(
                name = map["name"] as String,
                quantity = (map["quantity"] as Long).toInt()
            )
        }
    }
}
