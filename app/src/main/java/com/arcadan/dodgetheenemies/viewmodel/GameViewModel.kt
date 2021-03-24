package com.arcadan.dodgetheenemies.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence

class GameViewModel : ViewModel() {
    var showJoystick = ObservableBoolean(Persistence.instance.getBool(Keys.USEJOYSTICK))
}
