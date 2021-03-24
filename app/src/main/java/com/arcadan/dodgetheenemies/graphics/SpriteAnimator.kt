package com.arcadan.dodgetheenemies.graphics

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.arcadan.dodgetheenemies.objects.IPhysicalObject
import com.arcadan.dodgetheenemies.objects.Player
import com.arcadan.dodgetheenemies.objects.enemies.AlienBlack
import com.arcadan.dodgetheenemies.objects.enemies.AlienFrog
import com.arcadan.dodgetheenemies.objects.enemies.AlienSpaceShuttle
import com.arcadan.dodgetheenemies.objects.enemies.AlienWorm
import com.arcadan.dodgetheenemies.objects.enemies.Bat
import com.arcadan.dodgetheenemies.objects.enemies.Bee
import com.arcadan.dodgetheenemies.objects.enemies.BlueBird
import com.arcadan.dodgetheenemies.objects.enemies.BossBullet
import com.arcadan.dodgetheenemies.objects.enemies.BossDragon
import com.arcadan.dodgetheenemies.objects.enemies.Bunny
import com.arcadan.dodgetheenemies.objects.enemies.Chicken
import com.arcadan.dodgetheenemies.objects.enemies.Cobra
import com.arcadan.dodgetheenemies.objects.enemies.DragonBig
import com.arcadan.dodgetheenemies.objects.enemies.DragonLittle
import com.arcadan.dodgetheenemies.objects.enemies.Eagle
import com.arcadan.dodgetheenemies.objects.enemies.Fox
import com.arcadan.dodgetheenemies.objects.enemies.Ryno
import com.arcadan.dodgetheenemies.objects.enemies.Saw
import com.arcadan.dodgetheenemies.objects.enemies.SnowBall
import com.arcadan.dodgetheenemies.objects.enemies.TurtleBig
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG

internal class SpriteAnimator(
    private val animatedObject: IPhysicalObject,
    private val bitmapWithFrames: Bitmap,
    numberOfFramesHorizontally: Int,
    numberOfFramesVertically: Int,
    private val updateTimeMillis: Int,
    private val timeForOneFrameMillis: Double
) {
    private lateinit var animatorThread: AnimatorThread

    private var currentFrame = 0
    private var timeForCurrentFrameMillis = 0.0
    private var frameWidth = 0
    private var frameHeight = 0

    @Volatile
    var isRunning = false
        private set

    private var frames = ArrayList<Rect>()

    // split sprite image
    private fun splitBitmapIntoFrames(
        source: Bitmap,
        numberOfFramesHorizontally: Int,
        numberOfFramesVertically: Int
    ): ArrayList<Rect> {
        val destList = arrayListOf<Rect>()
        frameWidth = source.width / numberOfFramesHorizontally
        frameHeight = source.height / numberOfFramesVertically
        for (i in 0 until numberOfFramesVertically) for (j in 0 until numberOfFramesHorizontally) {
            destList.add(
                Rect(
                    j * frameWidth, i * frameHeight,
                    j * frameWidth + frameWidth, i * frameHeight + frameHeight
                )
            )
        }
        return destList
    }

    private inner class AnimatorThread : Thread() {
        override fun run() {
            while (isRunning) {
                timeForCurrentFrameMillis += updateTimeMillis.toDouble()
                if (timeForCurrentFrameMillis >= timeForOneFrameMillis) {
                    currentFrame = (currentFrame + 1) % frames.size
                    timeForCurrentFrameMillis -= timeForOneFrameMillis
                }
                val newBitmap = properCreateBitmap()
                when (animatedObject) {
                    is Player -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is BlueBird -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Cobra -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Chicken -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Bee -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Saw -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is AlienWorm -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is TurtleBig -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Bat -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Bunny -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is DragonLittle -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is DragonBig -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is AlienBlack -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is AlienSpaceShuttle -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is AlienFrog -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is BossDragon -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is BossBullet -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is SnowBall -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Fox -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Ryno -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                    is Eagle -> {
                        animatedObject.setNewSprite(newBitmap)
                    }
                }
                try {
                    sleep(updateTimeMillis.toLong())
                } catch (ignored: Exception) {
                }
            }
        }

        private fun properCreateBitmap(): Bitmap {

            return Bitmap.createBitmap(
                bitmapWithFrames, frames[currentFrame].left,
                frames[currentFrame].top, frameWidth,
                frameHeight
            )
        }
    }

    // start the thread
    fun startAnimation() {
        isRunning = true
        animatorThread = AnimatorThread()
        animatorThread.isDaemon = true
        animatorThread.start()
    }

    fun stopAnimation() {
        isRunning = false
        var flag = true

        while (flag) {
            try {
                animatorThread.join()
                flag = false
            } catch (exception: Exception) {
                LogHelper.log(TAG, "stopAnimation Exception: ${exception.stackTrace}", Log.ERROR)
            }
        }
    }

    init {
        frames = splitBitmapIntoFrames(
            bitmapWithFrames,
            numberOfFramesHorizontally,
            numberOfFramesVertically
        )
    }
}
