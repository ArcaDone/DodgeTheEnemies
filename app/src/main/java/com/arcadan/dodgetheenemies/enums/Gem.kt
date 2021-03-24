package com.arcadan.dodgetheenemies.enums

import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.game.GameParameters

enum class Gem(
    val topText: String,
    val image: Int,
    val iconImage: Int,
    val bottomText: String,
    val price: Int,
    val unit: PriceUnit,
    val reward: Int,
    val rewardType: Reward,
    val sku: String
) {
    LITTLE_GEMS(
        Dodgers.context!!.getString(R.string.hand_gems),
        R.drawable.shop_gem_little,
        R.drawable.ic_gem,
        GameParameters.instance.eightyGemReward.toString(),
        GameParameters.instance.firstGemPrice,
        PriceUnit.GEMS,
        GameParameters.instance.eightyGemReward,
        Reward.GEMS,
        GameParameters.instance.littleGemsSku
    ),
    MEDIUM_GEMS(
        Dodgers.context!!.getString(R.string.double_hand_gems),
        R.drawable.shop_gem_medium,
        R.drawable.ic_gem,
        GameParameters.instance.fiveHundredGemReward.toString(),
        GameParameters.instance.secondGemPrice,
        PriceUnit.MONEY,
        GameParameters.instance.fiveHundredGemReward,
        Reward.GEMS,
        GameParameters.instance.mediumGemsSku
    ),
    LARGE_GEMS(
        Dodgers.context!!.getString(R.string.bag_of_gems),
        R.drawable.shop_gem_large,
        R.drawable.ic_gem,
        GameParameters.instance.oneThousandTwoHundredGemsReward.toString(),
        GameParameters.instance.secondGemPrice,
        PriceUnit.MONEY,
        GameParameters.instance.oneThousandTwoHundredGemsReward,
        Reward.GEMS,
        GameParameters.instance.largeGemsSku
    ),
    LITTLE_CHEST_GEMS(
        Dodgers.context!!.getString(R.string.lot_of_gems),
        R.drawable.shop_gem_extra_large,
        R.drawable.ic_gem,
        GameParameters.instance.twoThousandFiveHundredGemReward.toString(),
        GameParameters.instance.secondGemPrice,
        PriceUnit.MONEY,
        GameParameters.instance.twoThousandFiveHundredGemReward,
        Reward.GEMS,
        GameParameters.instance.littleGemsChestSku
    ),
    MEDIUM_CHEST_GEMS(
        Dodgers.context!!.getString(R.string.box_of_gems),
        R.drawable.shop_gem_chest_one,
        R.drawable.ic_gem,
        GameParameters.instance.sixThousandFiveHundredGemReward.toString(),
        GameParameters.instance.secondGemPrice,
        PriceUnit.MONEY,
        GameParameters.instance.sixThousandFiveHundredGemReward,
        Reward.GEMS,
        GameParameters.instance.mediumGemsChestSku
    ),
    LARGE_CHEST_GEMS(
        Dodgers.context!!.getString(R.string.big_gem_chest),
        R.drawable.shop_gem_chest_two,
        R.drawable.ic_gem,
        GameParameters.instance.fourteenThousandGemsReward.toString(),
        GameParameters.instance.secondGemPrice,
        PriceUnit.MONEY,
        GameParameters.instance.fourteenThousandGemsReward,
        Reward.GEMS,
        GameParameters.instance.largeGemsChestSku
    )
}
