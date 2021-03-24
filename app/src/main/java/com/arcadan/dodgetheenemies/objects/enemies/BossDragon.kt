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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class BossDragon(override var sprite: Bitmap) : IPhysicalObject {

    companion object {
        val defaultSize: ObjectSize
            get() = ObjectSize(
                Global.instance.measures.width / 10,
                Global.instance.measures.height / 6
            )
    }

    override val isLethal: Boolean
        get() = true

    private var animator: SpriteAnimator? = null
    private var width = 0
    private var height = 0
    private var left = false

    // Jump time
    private lateinit var jumpJob: Job
    private val parentJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    private var _canJump: Boolean = true
    var canJump: Boolean
        get() = _canJump
        set(value) {
            LogHelper.log(TAG, "CanJump = $value", Log.VERBOSE)
            _canJump = value
        }

    override val hitbox: Rect
        get() = Rect(
            coordinates.x + 100,
            coordinates.y + 150,
            coordinates.x + size.width,
            coordinates.y + size.height * 2
        )

    override val shouldStopOnSurfaces: Boolean
        get() = true

    override fun runIntersectBehaviour(engine: GameEngine) {
        // Do Nothing
    }

    private var coords: VectorXY =
        VectorXY(0, 0)
    override var coordinates: VectorXY
        get() = coords
        set(value) {
            coords = value
        }

    override val size: ObjectSize
        get() = defaultSize
    override val shouldBeCleared: Boolean
        get() = TODO("Not yet implemented")

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

    // draw the character
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(sprite, coordinates.x.toFloat(), coordinates.y.toFloat(), null)
    }

    override fun update() {
        // Do nothing
    }

    override fun fall() {
        coordinates.y += GameParameters.instance.fallSpeed
    }

    fun initAnimator(
        bitmapWithFrames: Bitmap
    ) {
        animator = SpriteAnimator(
            this,
            bitmapWithFrames,
            7,
            2,
            100,
            100.0
        )
    }

    override fun startAnimation() {
        try {
            if (!animator!!.isRunning) animator!!.startAnimation()
        } catch (exception: Exception) {
            LogHelper.log(TAG, "Start animation Exception: ${exception.stackTrace}", Log.ERROR)
        }
    }

    override fun stopAnimation() {
        if (animator!!.isRunning) animator!!.stopAnimation()
    }

    // for animating character
    fun setNewSprite(newSprite: Bitmap) {
        if (left) {
            rotateBitmap(newSprite)
        } else {
            this.sprite = newSprite
        }
    }

    // for run to left
    private fun rotateBitmap(bitmap: Bitmap) {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        sprite = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // turn left
    fun mustMirrorImage(left: Boolean) {
        this.left = left
    }

    // run to left
    fun moveLeft() {
        var leftMovement = coordinates.x - 12 - speed
        val limitLeft = (hitbox.left - hitbox.right) / 2
        if (leftMovement < limitLeft) {
            leftMovement = limitLeft
        }
        coordinates.x = leftMovement
    }

    // run to right
    fun moveRight() {
        var rightMovement = coordinates.x + 12 + speed
        val limitRight = (hitbox.left - hitbox.right) / 2
        if (rightMovement > Global.instance.measures.width - width - limitRight) {
            rightMovement = Global.instance.measures.width - width - limitRight
        }
        coordinates.x = rightMovement
    }

    // Take a jump
    fun jump() {
    }

    init {
        speed = 0
        width = sprite.width / 4
        height = sprite.height / 3

        coordinates = VectorXY(
            Global.instance.measures.width,
            Global.instance.measures.height - height
        )
        initAnimator(sprite)
        startAnimation()
    }
}
