package com.arcadan.dodgetheenemies.objects.enemies

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import com.arcadan.dodgetheenemies.game.GameEngine
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.game.objects.ObjectSize
import com.arcadan.dodgetheenemies.game.objects.VectorXY
import com.arcadan.dodgetheenemies.graphics.SpriteAnimator
import com.arcadan.dodgetheenemies.objects.IPhysicalObject
import com.arcadan.dodgetheenemies.util.Global
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG

class Bunny(override var sprite: Bitmap) : IPhysicalObject {

    private var animator: SpriteAnimator? = null

    override val hitbox: Rect
        get() = Rect(
            coordinates.x,
            coordinates.y,
            coordinates.x + size.width / 4,
            coordinates.y + size.height / 4
        )

    override fun runIntersectBehaviour(engine: GameEngine) {
        // Do Nothing
    }

    override val isLethal: Boolean
        get() = true

    override val shouldStopOnSurfaces: Boolean
        get() = true

    private var coords: VectorXY =
        VectorXY(0, 0)
    override var coordinates: VectorXY
        get() = coords
        set(value) {
            coords = value
        }

    private var objectSize: ObjectSize =
        ObjectSize()
    override val size: ObjectSize
        get() = objectSize

    override val shouldBeCleared: Boolean
        get() = hitbox.left > Global.instance.measures.width

    private var objectSpeed: Int = 0
    override var speed: Int
        get() = objectSpeed
        set(value) {
            objectSpeed = value
        }

    private var objectShouldFall: Boolean = true
    override var shouldFall: Boolean
        get() = objectShouldFall
        set(value) {
            objectShouldFall = value
        }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(sprite, coordinates.x.toFloat(), coordinates.y.toFloat(), null)
    }

    override fun update() {
        coordinates.x += (speed + GameParameters.instance.deltaSpeed)
    }

    override fun fall() {
        coordinates.y += GameParameters.instance.fallSpeed
    }

    private fun initAnimator(bitmapWithFrames: Bitmap) {
        animator = SpriteAnimator(
            this,
            bitmapWithFrames,
            4,
            3,
            30,
            30.0
        )
    }

    override fun stopAnimation() {
        if (animator!!.isRunning) animator!!.stopAnimation()
    }

    override fun startAnimation() {
        try {
            if (!animator!!.isRunning) animator!!.startAnimation()
        } catch (exception: Exception) {
            LogHelper.log(TAG, "Start animation Exception: ${exception.stackTrace}", Log.ERROR)
        }
    }

    fun setNewSprite(newSprite: Bitmap) {
        rotateBitmap(newSprite)
    }

    // for run to left
    private fun rotateBitmap(bitmap: Bitmap) {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        sprite = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    init {
        speed = 6
        size.width = sprite.width
        size.height = sprite.height
        coordinates.x = 0
        coordinates.y = Global.instance.measures.lineBottomY - (size.height / 20)

        initAnimator(sprite)
        startAnimation()
    }
}
