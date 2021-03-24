package com.arcadan.dodgetheenemies.graphics

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.game.GameCallBacks
import com.arcadan.dodgetheenemies.game.GameEngine
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.game.GameThread
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.util.Global
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class GameViewSurface(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs),
    SensorEventListener, SurfaceHolder.Callback {

    lateinit var callBacks: GameCallBacks
    var level: Level? = null
    var gameThread: GameThread? = null
    private lateinit var gameEngine: GameEngine
    private lateinit var sensorManager: SensorManager
    lateinit var callBGE: GameEngineContext

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need implementation
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!Persistence.instance.getBool(Keys.USEJOYSTICK))
            gameEngine.onSensorChanged(event)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // No need implementation
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

        if (gameThread != null) {
            LogHelper.log(
                TAG, "surfaceDestroyed start destroy. \n " +
                        "Job1 is active? ${gameEngine.spawnJob.isActive} \n " +
                        "Job2 is active? ${gameEngine.spawnJob2.isActive}"
            )

            gameEngine.spawnJob.invokeOnCompletion { throwable ->
                if (throwable is CancellationException) {
                    LogHelper.log(TAG, "Coroutine1  was cancelled")
                }
            }
            gameEngine.spawnJob2.invokeOnCompletion { throwable ->
                if (throwable is CancellationException) {
                    LogHelper.log(TAG, "Coroutine2  was cancelled")
                }
            }

            CoroutineScope(Dispatchers.Default).launch {
                LogHelper.log(TAG, "CoroutineScope  started")
                gameEngine.coroutineScope.coroutineContext[Job]!!.cancelAndJoin() // wait until all jobs has finished
            }
            gameThread!!.shutdown()
            while (gameThread != null) {
                try {
                    gameThread!!.join()
                    if (!Persistence.instance.getBool(Keys.USEJOYSTICK))
                        sensorManager.unregisterListener(this)
                    gameThread = null
                } catch (ignored: Exception) {
                }
            }
        }
        GameParameters.instance.jumpEquationC = (Global.instance.measures.height / 10).toDouble()
        GameParameters.instance.reverseDirection = false
        GameParameters.instance.jumpGravity = 1
    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        Global.instance.measures =
            Measures(width, height)
        gameEngine = GameEngine(
            context,
            holder,
            level!!,
            resources
        )
        gameEngine.callBacks = callBacks
        callBGE.gameEngineCtx(gameEngine)
        gameThread = GameThread(gameEngine)
        gameThread!!.start()

        if (!Persistence.instance.getBool(Keys.USEJOYSTICK)) {
            // set up SensorManager
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

            sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gameEngine.onTouchEvent(event)
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    init {
        holder.addCallback(this)
    }
}

interface GameEngineContext {
    fun gameEngineCtx(gameEngine: GameEngine)
}
