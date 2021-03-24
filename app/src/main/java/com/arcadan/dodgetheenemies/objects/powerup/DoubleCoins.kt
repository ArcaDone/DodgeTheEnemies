package com.arcadan.dodgetheenemies.objects.powerup

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.game.GameEngine
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.game.objects.ObjectSize
import com.arcadan.dodgetheenemies.game.objects.VectorXY
import com.arcadan.dodgetheenemies.objects.IPhysicalObject
import com.arcadan.dodgetheenemies.util.Global
import kotlin.random.Random

class DoubleCoins(var resources: Resources) : IPhysicalObject {

    override val hitbox: Rect
        get() = Rect(
            coordinates.x, coordinates.y,
            coordinates.x + size.width, coordinates.y + size.height
        )

    override fun runIntersectBehaviour(engine: GameEngine) {
        engine.currentDoubleCoins++
    }

    override val isLethal: Boolean
        get() = false

    override val shouldStopOnSurfaces: Boolean
        get() = true

    private var objectSprite: Bitmap
    override val sprite: Bitmap
        get() = objectSprite

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

    override val shouldBeCleared: Boolean
        get() = hitbox.top > Global.instance.measures.height

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(sprite, coordinates.x.toFloat(), coordinates.y.toFloat(), null)
    }

    override fun update() {
        // Do nothing
    }

    override fun fall() {
        coordinates.y += GameParameters.instance.fallSpeed
    }

    override fun stopAnimation() {
        // do nothing
    }

    override fun startAnimation() {
        // do nothing
    }

    init {
        speed = 6
        val doubleCoinBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.power_up_double_coins)
        objectSprite = Bitmap.createScaledBitmap(
            doubleCoinBitmap,
            Global.instance.measures.coinWidth * 2,
            Global.instance.measures.coinWidth,
            false
        )
        size.width = sprite.width
        size.height = sprite.height
        val x = Random.nextInt(Global.instance.measures.width - size.width)
        coordinates = VectorXY(x, 0)
        doubleCoinBitmap.recycle()
    }
}
