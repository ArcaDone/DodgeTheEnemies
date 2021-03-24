package com.arcadan.dodgetheenemies.objects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.enums.JumpState
import com.arcadan.dodgetheenemies.game.GameEngine
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.game.objects.ObjectSize
import com.arcadan.dodgetheenemies.game.objects.VectorXY
import com.arcadan.dodgetheenemies.graphics.SpriteAnimator
import com.arcadan.dodgetheenemies.util.Global
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.arcadan.util.math.QuadraticEquation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Player(override var sprite: Bitmap) : IPhysicalObject {

    companion object {
        val defaultSize: ObjectSize
            get() = chooseObjectSize()

        private fun chooseObjectSize(): ObjectSize {
            return when (Persistence.instance.getInt(Keys.SELECTED_SKIN)) {
                0, 5, 6, 7, 8, 9 -> {
                    ObjectSize(
                        Global.instance.measures.width,
                        (Global.instance.measures.height * 5) / 3
                    )
                }
                else -> {
                    ObjectSize(
                        Global.instance.measures.width / 3,
                        Global.instance.measures.height * 2 / 3
                    )
                }
            }
        }
    }

    override val isLethal: Boolean
        get() = false

    private var animator: SpriteAnimator? = null
    private var width = 0
    private var height = 0
    private var left = false

    // player rect angle to determine the end of the game
    override val hitbox: Rect
        get() = chooseHitBox()

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
        if (shouldFall) {
            coordinates.y += GameParameters.instance.fallSpeed
        }
    }

    private fun initAnimator(
        bitmapWithFrames: Bitmap
    ) {
        animator = chooseAnimator(bitmapWithFrames)
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
        var leftMovement = coordinates.x - GameParameters.instance.playerSpeed - speed
        val limitLeft = getLimit()
        if (leftMovement < limitLeft) {
            leftMovement = limitLeft
        }
        coordinates.x = leftMovement
    }

    // run to right
    fun moveRight() {
        var rightMovement = coordinates.x + GameParameters.instance.playerSpeed + speed
        val limitRight = getLimit()
        if (rightMovement > Global.instance.measures.width - width - limitRight) {
            rightMovement = Global.instance.measures.width - width - limitRight
        }
        coordinates.x = rightMovement
    }

    // Take a jump
    fun jump() {
        if (GameParameters.instance.gameRunning) {
            LogHelper.log(TAG, "JUMP VALUE A: ${GameParameters.instance.jumpEquationA}")
            LogHelper.log(TAG, "JUMP VALUE B: ${GameParameters.instance.jumpEquationB}")
            LogHelper.log(TAG, "JUMP VALUE C: ${GameParameters.instance.jumpEquationC}")
            CoroutineScope(Dispatchers.Main).launch {
                if (jumpState == JumpState.NONE && !shouldFall) {
                    val equation = QuadraticEquation(
                        GameParameters.instance.jumpEquationA,
                        GameParameters.instance.jumpEquationB,
                        GameParameters.instance.jumpEquationC
                    )
                    val eqSolution = equation.quadraticRoots
                    LogHelper.log(TAG, equation.quadraticRoots.toString())
                    val x1 = eqSolution.x1 as Double
                    val x2 = eqSolution.x2 as Double
                    val root = if (x1 > x2) x1 else x2

                    // rising
                    jumpState = JumpState.RISING
                    val startY = coordinates.y
                    for (x in root.toInt() downTo 0) {
                        updateYValue(equation, x, startY)
                    }
                    jumpState = JumpState.FALLING
                    for (x in 0..root.toInt()) {
                        updateYValue(equation, x, startY)
                    }
                    jumpState = JumpState.NONE
                }
            }
        }
    }

    var jumpState: JumpState = JumpState.NONE

    private suspend fun updateYValue(equation: QuadraticEquation, x: Int, startY: Int) {
        while (!GameParameters.instance.gameRunning) {
            delay(1)
        }
        val y = equation.obtainYForXValue(x.toDouble()).toInt()
        coordinates.y = yJumpValue(startY, y)
        delay(GameParameters.instance.jumpGravity.toLong())
//        LogHelper.log(TAG, "The Y value for X $x is $y, relative player Y is ${coordinates.y}")
    }

    private fun yJumpValue(startY: Int, y: Int) =
        when (Persistence.instance.getInt(Keys.SELECTED_SKIN)) {
            0, 5, 6, 7, 8, 9 -> {
                startY - height / 2 - y
            }
            else -> {
                startY - height - y
            }
        }

    private fun chooseHitBox(): Rect {
        return when (Persistence.instance.getInt(Keys.SELECTED_SKIN)) {
            0, 5, 6, 9 -> {
                Rect(
                    coordinates.x + width / 3, coordinates.y + height / 9,
                    coordinates.x + width / 6 * 4, coordinates.y + height / 6 * 2
                )
            }
            7, 8 -> {
                Rect(
                    coordinates.x + width / 3, coordinates.y + height / 6,
                    coordinates.x + width / 6 * 4, coordinates.y + height / 6 * 3
                )
            }
            else -> {
                Rect(
                    coordinates.x + width / 4, coordinates.y + height / 5,
                    coordinates.x + width / 6 * 4, coordinates.y + height / 6 * 4
                )
            }
        }
    }

    private fun chooseAnimator(bitmapWithFrames: Bitmap): SpriteAnimator {
        return when (Persistence.instance.getInt(Keys.SELECTED_SKIN)) {
            0, 5, 6, 9 -> {
                SpriteAnimator(
                    this,
                    bitmapWithFrames,
                    4,
                    7,
                    30,
                    30.0
                )
            }
            7, 8 -> {
                SpriteAnimator(
                    this,
                    bitmapWithFrames,
                    4,
                    5,
                    30,
                    30.0
                )
            }
            else -> {
                SpriteAnimator(
                    this,
                    bitmapWithFrames,
                    4,
                    4,
                    30,
                    30.0
                )
            }
        }
    }

    private fun getLimit(): Int {
        return when (Persistence.instance.getInt(Keys.SELECTED_SKIN)) {
            0, 5, 6, 9 -> {
                (hitbox.left - hitbox.right)
            }
            else -> {
                (hitbox.left - hitbox.right) / 2
            }
        }
    }

    init {
        speed = 0
        width = sprite.width / 4
        height = sprite.height / 3

        coordinates = VectorXY(
            Global.instance.measures.width / 2,
            Global.instance.measures.height - height
        )
        initAnimator(sprite)
        startAnimation()
    }
}
