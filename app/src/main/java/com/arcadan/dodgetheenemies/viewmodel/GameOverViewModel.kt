package com.arcadan.dodgetheenemies.viewmodel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.service.MusicManager
import com.google.android.material.snackbar.Snackbar

class GameOverViewModel : ViewModel() {

    var backpack: MutableList<PowerUp> = mutableListOf()
    var errorState = MutableLiveData(false)
    var errorMessage = MutableLiveData(0)

    lateinit var gameModelJson: String

    fun playAgain(view: View) {
        if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
            MusicManager.pauseMusic()
            if (DataManager.instance.user.value!!.hearts >= GameParameters.instance.deltaHearts) {
                syncWithDataBase()
                backpack.let { GameParameters.instance.backpack.addAll(it) }
                decrementConsumable()
                val bundle = Bundle()
                bundle.putString("model", gameModelJson)
                view.findNavController().navigate(R.id.gameFragment, bundle)
            } else {
                Snackbar.make(view, R.string.not_enough_hearts, Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(
                view,
                Dodgers.context!!.getString(R.string.check_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    fun goHome(view: View) {
        Persistence.instance.saveInt(Keys.SELECTED_LEVEL, 0)
        view.findNavController().navigate(R.id.mainFragment)
    }

    fun fillBackpack(powerUp: PowerUp) {
        if (DataManager.instance.user.value!!.consumables[powerUp.ordinal].quantity >= 1) {
            if (backpack.contains(powerUp)) {
                backpack.remove(powerUp)
            } else {
                backpack.add(powerUp)
            }
        } else {
            errorMessage.value = R.string.not_enough_power_up
            errorState.value = true
        }
    }

    private fun decrementConsumable() {
        DataManager.instance.user.value!!.consumables[PowerUp.SLURP_RED.ordinal].quantity -= backpack.count { it.name == PowerUp.SLURP_RED.name }
        DataManager.instance.user.value!!.consumables[PowerUp.SPEED_UP.ordinal].quantity -= backpack.count { it.name == PowerUp.SPEED_UP.name }
        DataManager.instance.user.value!!.consumables[PowerUp.MEGA_JUMP.ordinal].quantity -= backpack.count { it.name == PowerUp.MEGA_JUMP.name }
        DataManager.instance.user.value!!.consumables[PowerUp.DOUBLE_COINS.ordinal].quantity -= backpack.count { it.name == PowerUp.DOUBLE_COINS.name }
    }

    private fun syncWithDataBase() {
        DataManager.instance.user.value!!.hearts -= GameParameters.instance.deltaHearts
        if (!DataManager.instance.user.value!!.heartsCount.timerHasStarted) {
            DataManager.instance.user.value!!.heartsCount.timerHasStarted = true
            DataManager.instance.user.value!!.heartsCount.localDateTime =
                System.currentTimeMillis() / 1000L
        }
        DataManager.instance.user.value!!.hearts = DataManager.instance.user.value!!.hearts
    }
}
