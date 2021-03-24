package com.arcadan.dodgetheenemies.objects

import android.graphics.Rect
import com.arcadan.dodgetheenemies.game.GameEngine

interface IPhysicalObject : IGameObject {
    val hitbox: Rect
    val isLethal: Boolean
    val shouldStopOnSurfaces: Boolean
    fun runIntersectBehaviour(engine: GameEngine)
}
