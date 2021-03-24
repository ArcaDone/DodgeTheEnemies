package com.arcadan.dodgetheenemies.viewmodel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.enums.Enemy
import com.arcadan.dodgetheenemies.enums.Feature
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.fragments.LevelFragmentDirections
import com.arcadan.dodgetheenemies.game.GameParameters
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.models.Rewards
import com.arcadan.dodgetheenemies.service.MusicManager.Companion.pauseMusic
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LevelViewModel : ViewModel() {

    var backpack: MutableList<PowerUp> = mutableListOf()
    val selectedLevel = MutableLiveData<Level>()
    val decrementHeartsAnimation = MutableLiveData(false)
    var errorState = MutableLiveData(false)
    var errorMessage = MutableLiveData(0)

    private var _items = MutableLiveData<List<Level>>()
    val items: LiveData<List<Level>>
        get() = _items

    init {
        fetchLevelsFromDatabase()
    }

    private fun fetchLevelsFromDatabase() {
        _items.value = DataManager.instance.levels
//        _items.value = DataManager.instance.levels + bossLevel() // Boss Disabled
    }

    private fun bossLevel(): Level {
        return Level(
            id = 23,
            levelBaseSpeed = 10,
            levelDeltaSpeed = 5,
            spawndelay = 3,
            levelVariation = 4,
            image = R.drawable.background_level_24,
            title = "Level 24",
            desc = "Boss",
            featureSet = mutableListOf(
                Feature.RUN,
                Feature.JUMP,
                Feature.SPAWN_COINS,
                Feature.RANDOM_SPAWN_DELAY,
                Feature.INCREASE_GAME_SPEED,
                Feature.SURFACE_ROCK
            ),
            enemySet = listOf(
                Enemy.BOSS_DRAGON,
                Enemy.ROCK_BIG
            ),
            levelDuration = 100,
            rewards = Rewards(
                coins = 20,
                hearts = 3,
                gems = 0
            ),
            powerUpSet = mutableListOf()
        )
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

    fun play() {
        if (Persistence.instance.getBool(Keys.CONNECTION_STATE)) {
            errorState.value = false
            syncWithDataBase()
            backpack.let { GameParameters.instance.backpack.addAll(it) }
            pauseMusic()
        } else {
            errorMessage.value = R.string.check_internet_connection
            errorState.value = true
        }
    }

    fun startLevel(view: View) {
        decrementConsumable()
        viewModelScope.launch {
            delay(1000)
            val levelChoose = Gson().toJson(selectedLevel.value)
            val bundle = Bundle()
            bundle.putString("model", levelChoose)
            bundle.putInt("currentGem", DataManager.instance.user.value!!.gems)
            view.findNavController().navigate(R.id.gameFragment, bundle)
        }
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

    fun discard() {
        backpack.clear()
        selectedLevel.value = _items.value!![0]
        Persistence.instance.saveInt(Keys.SELECTED_LEVEL, 0)
    }

    fun unlockedLevelError() {
        errorMessage.value = R.string.unlocked_level
        errorState.value = true
    }

    fun goBack(view: View) {
        view.findNavController()
            .navigate(LevelFragmentDirections.actionLevelFragmentToMainFragment())
    }
}
