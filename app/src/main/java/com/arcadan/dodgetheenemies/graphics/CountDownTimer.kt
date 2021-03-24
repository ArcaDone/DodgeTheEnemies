package com.arcadan.dodgetheenemies.graphics

import android.os.Handler
import android.os.HandlerThread
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class CountDownTimer(
    private var fromSeconds: Long,
    private val onCountDownListener: OnCountDownListener,
    private var delayInSeconds: Long = 1
) {

    private var seconds = 0L
    private var finished = false
    private var handler = Handler()
    private var handlerThread: HandlerThread? = null
    private var isBackgroundThreadRunning = false
    private val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val runnable = Runnable { decrementMinutes() }

    init {
        check(!(fromSeconds <= 0)) { javaClass.simpleName + " can't work in state 0:00" }

        if (delayInSeconds <= 0)
            delayInSeconds = 1

        setCountDownValues()
    }

    private fun setCountDownValues(
        fromSeconds: Long = this.fromSeconds
    ) {
        this.fromSeconds = fromSeconds

        if (fromSeconds <= 0) {
            seconds = 59
            return
        }
        seconds = this.fromSeconds
    }

    fun getSecondsTillCountDown() = seconds

    fun setTimerPattern(pattern: String) {
        if (pattern.equals("mm:ss", ignoreCase = true) || pattern.equals(
                "m:s",
                ignoreCase = true
            ) || pattern.equals("mm", ignoreCase = true) ||
            pattern.equals("ss", ignoreCase = true) || pattern.equals(
                "m",
                ignoreCase = true
            ) || pattern.equals("s", ignoreCase = true)
        ) simpleDateFormat.applyPattern(pattern)
    }

    fun runOnBackgroundThread() {
        if (isBackgroundThreadRunning) return
        handlerThread = HandlerThread(javaClass.simpleName)
        startBackgroundThreadIfNotRunningAndEnabled()
        handler = Handler(handlerThread!!.looper)
    }

    private fun startBackgroundThreadIfNotRunningAndEnabled() {

        handlerThread!!.run {
            start()
            isBackgroundThreadRunning = true
        }
    }

    private fun getCountDownTime(): Int {
        return seconds.toInt()
    }

    private fun decrementMinutes() {
        seconds--

        if (seconds == 0L) {
            finish()
        }
        runCountdown()
    }

    private fun finish() {
        onCountDownListener.onCountDownFinished()
        finished = true
        pause()
    }

    private fun decrementSeconds() {
        handler.postDelayed(
            runnable,
            TimeUnit.SECONDS.toMillis(delayInSeconds)
        )
    }

    fun start(resume: Boolean = false) {
        if (!resume) {
            setCountDownValues()
            finished = false
        }
        runCountdown()
    }

    private fun runCountdown() {
        if (!finished) {
            updateUI()
            decrementSeconds()
        }
    }

    private fun updateUI() {
        onCountDownListener.onCountDownActive(getCountDownTime())
    }

    fun pause() {
        handler.removeCallbacks(runnable)
    }

    interface OnCountDownListener {
        fun onCountDownActive(time: Int)
        fun onCountDownFinished()
    }
}
