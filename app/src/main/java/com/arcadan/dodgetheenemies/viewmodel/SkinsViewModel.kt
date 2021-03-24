package com.arcadan.dodgetheenemies.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.enums.SkinEnum
import com.arcadan.dodgetheenemies.fragments.SkinsFragmentDirections
import com.arcadan.dodgetheenemies.models.Skin

class SkinsViewModel : ViewModel() {
    var observedSwitchMusicState = ObservableBoolean(false)
    var observedJoystickState = ObservableBoolean(false)
    private var skinModels: MutableList<Skin> = mutableListOf()

    private var _items = MutableLiveData<List<Skin>>()
    val items: LiveData<List<Skin>>
        get() = _items

    init {
        observedSwitchMusicState.set(Persistence.instance.getBool(Keys.MUSIC_STATE))
        observedJoystickState.set(Persistence.instance.getBool(Keys.USEJOYSTICK))
        showDodgersSkinItems()
    }

    private fun showDodgersSkinItems() {
        skinModels.clear()
        for (enum in SkinEnum.values()) {
            skinModels.add(Skin(enum.id, enum.skinName, enum.image, "test"))
        }
        _items.value = skinModels
    }

    fun backButtonClick(view: View) { view.findNavController()
        .navigate(SkinsFragmentDirections.actionSkinsFragmentToMainFragment())
    }
}
