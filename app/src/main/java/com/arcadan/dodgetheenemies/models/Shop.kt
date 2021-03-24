package com.arcadan.dodgetheenemies.models

import com.arcadan.dodgetheenemies.enums.PriceUnit
import com.arcadan.dodgetheenemies.enums.Reward

data class Shop(
    var name: String,
    var topText: String,
    var image: Int,
    var iconImage: Int,
    var bottomText: String,
    var price: Int,
    var unit: PriceUnit,
    var reward: Int = 0,
    var rewardType: Reward = Reward.NONE,
    var sku: String = ""
)
