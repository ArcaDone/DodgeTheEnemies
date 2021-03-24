package com.arcadan.dodgetheenemies.enums

import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R

enum class Consumables(
    val image: Int,
    val title: String,
    val description: String
) {
    SLURP_RED(
        R.drawable.power_up_slurp_red,
        "Slurp",
        Dodgers.context!!.getString(R.string.slurp_description)),
    SPEED_UP(
        R.drawable.power_up_speed_up,
        "Speed Up",
        Dodgers.context!!.getString(R.string.speed_up_description)),
    MEGA_JUMP(
        R.drawable.power_up_mega_jump,
        "Mega Jump",
        Dodgers.context!!.getString(R.string.mega_jump_description)),
    DOUBLE_COINS(
        R.drawable.power_up_double_coins,
        "Double Coins",
        Dodgers.context!!.getString(R.string.double_coins_description)),
    SOON(R.drawable.soon, "Coming", "Soon")
}
