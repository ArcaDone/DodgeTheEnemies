package com.arcadan.dodgetheenemies.objects.enemies

import android.graphics.Bitmap
import android.graphics.Canvas
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

class TurtleBig(override var sprite: Bitmap) : IPhysicalObject {

    private var animator: SpriteAnimator? = null

    override val hitbox: Rect
        get() = Rect(
            coordinates.x,
            coordinates.y + size.height / 20,
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
        get() = hitbox.right < 0

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
        coordinates.x -= (speed + GameParameters.instance.deltaSpeed)
    }

    override fun fall() {
        coordinates.y += GameParameters.instance.fallSpeed
    }

    override fun stopAnimation() {
        if (animator!!.isRunning) animator!!.stopAnimation()
    }

    private fun initAnimator(bitmapWithFrames: Bitmap) {
        animator = SpriteAnimator(
            this,
            bitmapWithFrames,
            4,
            4,
            30,
            30.0
        )
    }

    override fun startAnimation() {
        try {
            if (!animator!!.isRunning) animator!!.startAnimation()
        } catch (exception: Exception) {
            LogHelper.log(TAG, "Start animation Exception: ${exception.stackTrace}", Log.ERROR)
        }
    }

    fun setNewSprite(newSprite: Bitmap) {
        this.sprite = newSprite
    }

    init {
        speed = 8
        size.width = sprite.width
        size.height = sprite.height
        coordinates.x = Global.instance.measures.width
        coordinates.y = Global.instance.measures.lineBottomY - (size.height / 20)

        initAnimator(sprite)
        startAnimation()
    }
}
