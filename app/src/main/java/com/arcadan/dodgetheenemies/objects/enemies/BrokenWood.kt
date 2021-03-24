package com.arcadan.dodgetheenemies.objects.enemies

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

class BrokenWood constructor(var resources: Resources) :
    IPhysicalObject {

    override val hitbox: Rect
        get() = Rect(
            coordinates.x,
            coordinates.y,
            coordinates.x + size.width,
            coordinates.y + size.height
        )

    override fun runIntersectBehaviour(engine: GameEngine) {
        // Do Nothing
    }

    override val isLethal: Boolean
        get() = true

    override val shouldStopOnSurfaces: Boolean
        get() = false

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

    // enemies reach the ground
    override val shouldBeCleared: Boolean
        get() = hitbox.bottom > Global.instance.measures.groundY

    override fun update() {
        coordinates.y += (speed + GameParameters.instance.deltaSpeed)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(sprite, coordinates.x.toFloat(), coordinates.y.toFloat(), null)
    }

    override fun fall() {
        coordinates.y += GameParameters.instance.fallSpeed / 2
    }

    override fun stopAnimation() {
        // do nothing
    }

    override fun startAnimation() {
        // do nothing
    }

    init {
        speed = 8
        val enemyBitmap = BitmapFactory.decodeResource(resources, R.drawable.enemy_broked_wood)
        objectSprite = Bitmap.createScaledBitmap(
            enemyBitmap,
            Global.instance.measures.bulletWidth, Global.instance.measures.bulletHeight,
            false
        )
        size.width = sprite.width
        size.height = sprite.height
        // create random position of the enemy from the top side of the screen
        val x = Random.nextInt(Global.instance.measures.width - size.width)
        coordinates = VectorXY(x, 0)
        enemyBitmap.recycle()
    }
}
