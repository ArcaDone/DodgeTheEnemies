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
import kotlin.random.Random

class Cobra constructor(override var sprite: Bitmap) :
    IPhysicalObject {

    private var animator: SpriteAnimator? = null
    private var rightMovement: Boolean

    override val hitbox: Rect
        get() = Rect(
            coordinates.x + size.width / 10,
            coordinates.y + size.width / 10,
            coordinates.x + size.width / 5,
            coordinates.y + size.height / 6
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
        get() = if (rightMovement) {
            hitbox.left > Global.instance.measures.width
        } else {
            hitbox.right < 0
        }

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
        if (rightMovement) {
            coordinates.x += (speed + GameParameters.instance.deltaSpeed)
        } else {
            coordinates.x -= (speed + GameParameters.instance.deltaSpeed)
        }
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

    private fun rotateBitmap(bitmap: Bitmap) {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        sprite = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun setNewSprite(newSprite: Bitmap) {
        if (rightMovement) {
            rotateBitmap(newSprite)
        } else {
            this.sprite = newSprite
        }
    }

    init {
        speed = 7
        size.width = sprite.width
        size.height = sprite.height
        rightMovement = Random.nextBoolean()
        if (rightMovement) {
            coordinates.x = 0
        } else {
            coordinates.x = Global.instance.measures.width
        }
        coordinates.y = Global.instance.measures.lineBottomY - size.height

        initAnimator(sprite)
        startAnimation()
    }
}
