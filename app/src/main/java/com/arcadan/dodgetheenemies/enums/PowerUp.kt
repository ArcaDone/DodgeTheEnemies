package com.arcadan.dodgetheenemies.enums

import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.game.GameParameters

enum class PowerUp(
    val topText: String,
    val image: Int,
    val iconImage: Int,
    val bottomText: String,
    val price: Int,
    val unit: PriceUnit,
    val reward: Int,
    val rewardType: Reward
) {
    SLURP_RED("Slurp Red", R.drawable.power_up_slurp_red, R.drawable.ic_coin, "${GameParameters.instance.slurpRedPrice}", GameParameters.instance.slurpRedPrice, PriceUnit.COINS, GameParameters.instance.slurpRedReward, Reward.POWER_UP),
    SPEED_UP("Speed Up", R.drawable.power_up_speed_up, R.drawable.ic_coin, "${GameParameters.instance.speedUpPrice}", GameParameters.instance.speedUpPrice, PriceUnit.COINS, GameParameters.instance.speedUpReward, Reward.POWER_UP),
    MEGA_JUMP("Mega Jump", R.drawable.power_up_mega_jump, R.drawable.ic_coin, "${GameParameters.instance.megaJumpPrice}", GameParameters.instance.megaJumpPrice, PriceUnit.COINS, GameParameters.instance.megaJumpReward, Reward.POWER_UP),
    DOUBLE_COINS("Double Coins", R.drawable.power_up_double_coins, R.drawable.ic_coin, "${GameParameters.instance.doubleCoinsPrice}", GameParameters.instance.doubleCoinsPrice, PriceUnit.COINS, GameParameters.instance.doubleCoinsReward, Reward.POWER_UP),
}
