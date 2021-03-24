package com.arcadan.dodgetheenemies.enums

import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.game.GameParameters

enum class Heart(
    val topText: String,
    val image: Int,
    val iconImage: Int,
    val bottomText: String,
    val price: Int,
    val unit: PriceUnit,
    val reward: Int,
    val rewardType: Reward
) {
    TEN("${GameParameters.instance.twentyHeartsReward}", R.drawable.shop_heart_little, R.drawable.ic_multimedia, Dodgers.context!!.getString(R.string.watch_me), GameParameters.instance.tenHeartsPrice, PriceUnit.NONE, GameParameters.instance.twentyHeartsReward, Reward.HEARTS),
    TWENTY("${GameParameters.instance.twentyHeartsReward}", R.drawable.shop_heart_medium, R.drawable.ic_coin, "${GameParameters.instance.twentyHeartsPrice}", GameParameters.instance.twentyHeartsPrice, PriceUnit.COINS, GameParameters.instance.twentyHeartsReward, Reward.HEARTS),
    FORTY("${GameParameters.instance.fortyHeartsReward}", R.drawable.shop_heart_large, R.drawable.ic_coin, "${GameParameters.instance.fortyHeartsPrice}", GameParameters.instance.fortyHeartsPrice, PriceUnit.COINS, GameParameters.instance.fortyHeartsReward, Reward.HEARTS),
    SIXTY("${GameParameters.instance.sixtyHeartsReward}", R.drawable.shop_heart_extra_large, R.drawable.ic_coin, "${GameParameters.instance.sixtyHeartsPrice}", GameParameters.instance.sixtyHeartsPrice, PriceUnit.COINS, GameParameters.instance.sixtyHeartsReward, Reward.HEARTS),
    EIGHTY("${GameParameters.instance.eightyHeartsReward}", R.drawable.shop_heart_chest_one, R.drawable.ic_coin, "${GameParameters.instance.eightyHeartsPrice}", GameParameters.instance.eightyHeartsPrice, PriceUnit.COINS, GameParameters.instance.eightyHeartsReward, Reward.HEARTS),
    ONEHUNDRED("${GameParameters.instance.oneHundredHearReward}", R.drawable.shop_heart_chest_two, R.drawable.ic_coin, "${GameParameters.instance.oneHundredHeartsPrice}", GameParameters.instance.oneHundredHeartsPrice, PriceUnit.COINS, GameParameters.instance.oneHundredHearReward, Reward.HEARTS),
}
