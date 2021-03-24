package com.arcadan.dodgetheenemies.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence

class MusicManager {

    companion object {
        var musicService: MusicService? = null
        var mIsBound = false

        fun bindMusic() {
            // Bind Music Service
            if (Persistence.instance.getBool(Keys.MUSIC_STATE)) {
                doBindService()
            } else if (musicService != null) {
                musicService!!.pauseMusic()
            }
        }

        private fun doBindService() {
            Dodgers.context!!.bindService(
                Intent(Dodgers.context, MusicService::class.java),
                serviceConnection, Context.BIND_AUTO_CREATE
            )
            mIsBound = true
        }

        private val serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                musicService = (service as MusicService.ServiceBinder).service
                if (musicService != null) {
                    musicService!!.resumeMusic()
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                musicService = null
            }
        }

        fun pauseMusic() {
            if (musicService != null && Persistence.instance.getBool(Keys.MUSIC_STATE)) {
                musicService!!.pauseMusic()
            }
        }

        fun resumeMusic() {
            if (musicService != null && Persistence.instance.getBool(Keys.MUSIC_STATE)) {
                musicService!!.resumeMusic()
            }
        }
    }
}
