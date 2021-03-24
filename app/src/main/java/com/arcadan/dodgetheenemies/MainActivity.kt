package com.arcadan.dodgetheenemies

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.arcadan.dodgetheenemies.data.AuthManager
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.pauseMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.resumeMusic
import com.arcadan.dodgetheenemies.service.NetworkListener
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private val broadcastReceiver by lazy {
        NetworkListener.create({
            Persistence.instance.saveBool(Keys.CONNECTION_STATE, true)
        }, {
            Persistence.instance.saveBool(Keys.CONNECTION_STATE, false)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this) {}

        val appUpdateManager = AppUpdateManagerFactory.create(Dodgers.context!!)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE, // AppUpdateType.FLEXIBLE
                    this,
                    0
                )
            }
        }

        if (Persistence.instance.getInt(Keys.USER_ID) == 0) {
            AuthManager.generateUser()
            Persistence.instance.saveInt(Keys.SELECTED_SKIN, 0)
            Persistence.instance.saveBool(Keys.MUSIC_STATE, true)
        } else {
            DataManager.instance.initDataManager(Persistence.instance.getInt(Keys.USER_ID))
        }
        DataManager.instance.initLevels()

        setVisibilityFlags()
        window.decorView.setOnSystemUiVisibilityChangeListener {
            setVisibilityFlags()
        }
    }

    override fun onResume() {
        super.onResume()
        resumeMusic()
        NetworkListener.register(Dodgers.context!!, broadcastReceiver)
    }

    override fun onPause() {
        super.onPause()
        pauseMusic()
        NetworkListener.unregister(Dodgers.context!!, broadcastReceiver)
    }

    private fun setVisibilityFlags() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
