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

class AlienNuke constructor(var resources: Resources, spawnCoordinates: VectorXY) :
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

    override val shouldBeCleared: Boolean
        get() = hitbox.bottom > Global.instance.measures.groundY

    override fun update() {
        // Do nothing
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(sprite, coordinates.x.toFloat(), coordinates.y.toFloat(), null)
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
        speed = 3
        val enemyBitmap = BitmapFactory.decodeResource(resources, R.drawable.enemy_nuke)
        objectSprite = Bitmap.createScaledBitmap(
            enemyBitmap,
            Global.instance.measures.rockWidth, Global.instance.measures.rockHeight * 2,
            false
        )
        size.width = sprite.width
        size.height = sprite.height
        coordinates.x = spawnCoordinates.x + sprite.width
        coordinates.y = spawnCoordinates.y
        enemyBitmap.recycle()
    }
}
