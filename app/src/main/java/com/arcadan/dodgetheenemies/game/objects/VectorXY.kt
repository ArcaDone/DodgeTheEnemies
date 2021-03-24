package com.arcadan.dodgetheenemies.game.objects

class VectorXY internal constructor(var x: Int, var y: Int) {
    operator fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun set(src: VectorXY) {
        x = src.x
        y = src.y
    }

    override fun toString(): String {
        return "X: $x, Y: $y"
    }
}
