package com.arcadan.dodgetheenemies.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.bindMusic
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.mIsBound
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.musicService

class SettingsViewModel : ViewModel() {
    var observedSwitchMusicState = ObservableBoolean(false)
    var observedJoystickState = ObservableBoolean(false)

    init {
        observedSwitchMusicState.set(Persistence.instance.getBool(Keys.MUSIC_STATE))
        observedJoystickState.set(Persistence.instance.getBool(Keys.USEJOYSTICK))
    }

    fun onCheckedChanged(check: Boolean) {
        if (check) {
            if (mIsBound && musicService != null) {
                musicService!!.resumeMusic()
            } else {
                Persistence.instance.saveBool(Keys.MUSIC_STATE, true)
                bindMusic()
            }
        } else {
            musicService!!.pauseMusic()
        }

        Persistence.instance.saveBool(Keys.MUSIC_STATE, check)
    }

    fun onJoystickCheckedChanged(check: Boolean) {
        if (check) {
            Persistence.instance.saveBool(Keys.USEJOYSTICK, true)
        } else {
            Persistence.instance.saveBool(Keys.USEJOYSTICK, false)
        }
    }

    fun backButtonClick(view: View) {
        view.findNavController().navigate(R.id.mainFragment)
    }
}
