package com.arcadan.dodgetheenemies.objects.surfaces

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.game.GameEngine
import com.arcadan.dodgetheenemies.game.objects.ObjectSize
import com.arcadan.dodgetheenemies.game.objects.VectorXY
import com.arcadan.dodgetheenemies.objects.IPhysicalObject
import com.arcadan.dodgetheenemies.util.Global
import kotlin.random.Random

class SurfaceWood(val resources: Resources) : IPhysicalObject {
    override val hitbox: Rect
        get() = Rect(
            coordinates.x, coordinates.y,
            coordinates.x + size.width, coordinates.y + size.height
        )

    override val isLethal: Boolean
        get() = false

    override val shouldStopOnSurfaces: Boolean
        get() = true

    override fun runIntersectBehaviour(engine: GameEngine) {
        // Do nothing
    }

    private var coords: VectorXY =
        VectorXY(0, 0)
    override var coordinates: VectorXY
        get() = coords
        set(value) {
            coords = value
        }

    private var objectSprite: Bitmap
    override val sprite: Bitmap
        get() = objectSprite

    private var objectSize: ObjectSize =
        ObjectSize()
    override val size: ObjectSize
        get() = objectSize

    override val shouldBeCleared: Boolean
        get() = hitbox.bottom > Global.instance.measures.groundY

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
        // Do nothing
    }

    override fun fall() {
        // Do nothing
    }

    override fun stopAnimation() {
        // do nothing
    }

    override fun startAnimation() {
        // do nothing
    }

    init {
        val surfaceBitmap = BitmapFactory.decodeResource(resources, R.drawable.surface_block_4)
        objectSprite = Bitmap.createScaledBitmap(
            surfaceBitmap,
            Global.instance.measures.blockWidth, Global.instance.measures.blockHeight,
            false
        )
        size.width = sprite.width
        size.height = sprite.height
        // create random position of the enemy from the top side of the screen
        val x = Random.nextInt(Global.instance.measures.width - size.width)
        val y = Random.nextInt(
            Global.instance.measures.groundY / 4 * 3,
            Global.instance.measures.groundY - size.height
        )
        coordinates = VectorXY(x, y)
        surfaceBitmap.recycle()
    }
}
