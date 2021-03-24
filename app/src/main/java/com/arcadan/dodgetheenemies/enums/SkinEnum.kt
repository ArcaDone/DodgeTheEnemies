package com.arcadan.dodgetheenemies.enums

import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.game.GameParameters

enum class SkinEnum(
    val id: Int,
    val skinName: String,
    val skinDescription: String,
    val topText: String,
    val image: Int,
    val iconImage: Int,
    val bottomText: String,
    val price: Int,
    val unit: PriceUnit
) {
    SKIN_FIRST(0, "Main D", "It's an Hydraulic", "Soon", R.drawable.miniature_main_dodger, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_SECOND(1, "Ink Addict", "It's Mario's brother", "Soon", R.drawable.miniature_gaek_tattoo, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_THIRD(2, "Tentazioni", "It's Mario's brother", "Soon", R.drawable.miniature_chef, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_FOURTH(3, "Jumper", "It's Mario's brother", "Soon", R.drawable.miniature_jumper_character, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_FIFTH(4, "Carlo's Dodger", "It's Mario's brother", "Soon", R.drawable.miniature_main, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_MUMMY(5, "LeftMummy", "It's Mario's brother", "Soon", R.drawable.miniature_mummy, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_SKELETON(6, "Strangers Fingers", "It's Mario's brother", "Soon", R.drawable.miniature_skeleton, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_SNOWMAN(7, "Snowman", "It's Mario's brother", "Soon", R.drawable.miniature_snowman, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_SANTACLOUS(8, "Santa Clous", "It's Mario's brother", "Soon", R.drawable.miniature_santa_clous, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
    SKIN_GLOVE_WINTER(9, "Guanto", "It's Mario's brother", "Soon", R.drawable.miniature_glove_winter, R.drawable.power_slurp_disabled, "${GameParameters.instance.firstSkinPrice} ${PriceUnit.COINS.value}", GameParameters.instance.firstSkinPrice, PriceUnit.COINS),
}
