package com.arcadan.dodgetheenemies.objects

import android.graphics.Bitmap
import android.graphics.Canvas
import com.arcadan.dodgetheenemies.game.objects.ObjectSize
import com.arcadan.dodgetheenemies.game.objects.VectorXY

interface IGameObject {
    val sprite: Bitmap
    var coordinates: VectorXY
    val size: ObjectSize
    var speed: Int
    var shouldFall: Boolean
    val shouldBeCleared: Boolean
    fun draw(canvas: Canvas)
    fun update()
    fun fall()
    fun stopAnimation()
    fun startAnimation()
}
