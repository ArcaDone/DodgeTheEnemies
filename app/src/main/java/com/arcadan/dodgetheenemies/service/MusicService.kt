package com.arcadan.dodgetheenemies.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.arcadan.dodgetheenemies.R
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG

class MusicService : Service(), MediaPlayer.OnErrorListener {
    private var length = 0
    private val mBinder: IBinder = ServiceBinder()
    var mPlayer: MediaPlayer? = null

    override fun onBind(arg0: Intent): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        LogHelper.log(TAG, "Music Service started", Log.DEBUG)
        mPlayer = MediaPlayer.create(this, R.raw.main_music)
        mPlayer!!.setOnErrorListener(this)
        if (mPlayer != null) {
            mPlayer!!.isLooping = true
            mPlayer!!.setVolume(50f, 50f)
        }
        mPlayer!!.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
                onError(mPlayer!!, what, extra)
                return true
            }
        })
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (mPlayer != null) {
            mPlayer!!.start()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayer != null) {
            try {
                mPlayer!!.stop()
                mPlayer!!.release()
            } finally {
                mPlayer = null
            }
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show()
        if (mPlayer != null) {
            try {
                mPlayer!!.stop()
                mPlayer!!.release()
            } finally {
                mPlayer = null
            }
        }
        return false
    }

    fun pauseMusic() {
        if (mPlayer != null) {
            if (mPlayer!!.isPlaying) {
                mPlayer!!.pause()
                length = mPlayer!!.currentPosition
            }
        }
    }

    fun resumeMusic() {
        if (mPlayer != null) {
            if (!mPlayer!!.isPlaying) {
                mPlayer!!.seekTo(length)
                mPlayer!!.start()
            }
        }
    }

    inner class ServiceBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }
}
