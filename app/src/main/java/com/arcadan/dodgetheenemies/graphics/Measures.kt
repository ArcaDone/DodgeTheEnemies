package com.arcadan.dodgetheenemies.graphics

import android.graphics.Rect

class Measures(val width: Int, val height: Int) {

    val backgroundRect: Rect
        get() = Rect(0, 0, width, height)

    val groundY: Int
        get() = height / 9 * 8

    // horizontal enemies size
    val arrowWidth: Int
        get() = width / 15

    val arrowHeight: Int
        get() = height / 100 * 7

    val spikyMonsterGround1Width: Int
        get() = width / 16

    val spikyMonsterGround1Height: Int
        get() = height / 20

    val ufoGround2Width: Int
        get() = width / 15

    val ufoGround2Height: Int
        get() = height / 20

    val bulletWidth: Int
        get() = width / 20

    val bulletHeight: Int
        get() = height / 25

    val lineBottomY: Int
        get() = height / 40 * 20

    val rockWidth: Int
        get() = width / 24

    val rockHeight: Int
        get() = height / 10

    val blockWidth: Int
        get() = width / 5

    val blockHeight: Int
        get() = height / 20

    val coinWidth: Int
        get() = width / 30

    val starWidth: Int
        get() = width / 25

    val slurpWidth: Int
        get() = width / 28

    val megaJumpWidth: Int
        get() = width / 30

    val megaJumpHeight: Int
        get() = width / 25

    val eggWidth: Int
        get() = width / 40

    val eggHeight: Int
        get() = height / 20
}
