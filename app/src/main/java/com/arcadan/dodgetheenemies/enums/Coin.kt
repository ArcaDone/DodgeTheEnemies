package com.arcadan.dodgetheenemies.enums

import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.game.GameParameters

enum class Coin(
    val topText: String,
    val image: Int,
    val iconImage: Int,
    val bottomText: String,
    val price: Int,
    val unit: PriceUnit,
    val reward: Int,
    val rewardType: Reward
) {
    LITTLE_REWARD("${GameParameters.instance.littleCoinsReward}", R.drawable.shop_gold_little, R.drawable.ic_gem, "${GameParameters.instance.littleCoinPrice} ", GameParameters.instance.littleCoinPrice, PriceUnit.GEMS, GameParameters.instance.littleCoinsReward, Reward.COINS),
    MEDIUM_REWARD("${GameParameters.instance.mediumCoinsReward}", R.drawable.shop_gold_medium, R.drawable.ic_gem, "${GameParameters.instance.mediumCoinPrice} ", GameParameters.instance.mediumCoinPrice, PriceUnit.GEMS, GameParameters.instance.mediumCoinsReward, Reward.COINS),
    LARGE_REWARD("${GameParameters.instance.largeCoinsReward}", R.drawable.shop_gold_big, R.drawable.ic_gem, "${GameParameters.instance.largeCoinPrice} ", GameParameters.instance.largeCoinPrice, PriceUnit.GEMS, GameParameters.instance.largeCoinsReward, Reward.COINS),
    LITTLE_CHEST_REWARD("${GameParameters.instance.littleCoinsChestReward}", R.drawable.shop_chest_gold_one, R.drawable.ic_gem, "${GameParameters.instance.littleChestPrice} ", GameParameters.instance.littleChestPrice, PriceUnit.GEMS, GameParameters.instance.littleCoinsChestReward, Reward.COINS),
    MEDIUM_CHEST_REWARD("${GameParameters.instance.mediumCoinsChestReward}", R.drawable.shop_chest_gold_two, R.drawable.ic_gem, "${GameParameters.instance.mediumChestPrice} ", GameParameters.instance.mediumChestPrice, PriceUnit.GEMS, GameParameters.instance.mediumCoinsChestReward, Reward.COINS),
    LARGE_CHEST_REWARD("${GameParameters.instance.largeCoinsChestReward}", R.drawable.shop_chest_gold_three, R.drawable.ic_gem, "${GameParameters.instance.largeChestPrice} ", GameParameters.instance.largeChestPrice, PriceUnit.GEMS, GameParameters.instance.largeCoinsChestReward, Reward.COINS),
}
