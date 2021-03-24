package com.arcadan.dodgetheenemies.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.arcadan.dodgetheenemies.Dodgers
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.models.User
import com.arcadan.dodgetheenemies.enums.Enemy
import com.arcadan.dodgetheenemies.enums.Feature
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.models.Rewards
import com.arcadan.util.BuildConfig
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class DataManager {
    companion object {
        var instance = DataManager()
    }

    private var _user = MutableLiveData<User>()
    var user: MutableLiveData<User>
        get() = _user
        set(value) {
            _user = value
            DatabaseManager.setValue("", _user.value!!.toHashMap())
        }

    fun setUserSilent(user: User) {
        _user.postValue(user)
    }

    private var _levels = mutableListOf<Level>()
    var levels: MutableList<Level>
        get() = _levels
        set(value) {
            _levels = value
        }

    private lateinit var reference: DatabaseReference

    fun initDataManager(userId: Int) {
        reference = Firebase.database.getReference(checkIfDebugUser(userId))
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue<HashMap<String, Any>>()
                LogHelper.log(TAG, "Value is: $value")
                _user.value = value?.let { User.fromHashMap(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                LogHelper.log(TAG, "Failed to read value user", Log.ERROR)
            }
        })
    }

    private fun checkIfDebugUser(userId: Int) =
        if (BuildConfig.DEBUG)
            "debugUsers/$userId"
        else
            "users/$userId"

    fun initLevels() {
        _levels.addAll(
            listOf(
                Level(
                    id = 0,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 3,
                    image = R.drawable.background_level_1,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 1",
                    desc = Dodgers.context!!.getString(R.string.new_experience),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY
                    ),
                    enemySet = listOf(
                        Enemy.ROCKS
                    ),
                    levelDuration = 15,
                    rewards = Rewards(
                        coins = 20,
                        hearts = 0,
                        gems = 0
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 1,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 3,
                    image = R.drawable.background_level_2,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 2",
                    desc = Dodgers.context!!.getString(R.string.city_life),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.SURFACE_WOOD
                    ),
                    enemySet = listOf(
                        Enemy.SPIKY_MONSTERS
                    ),
                    levelDuration = 20,
                    rewards = Rewards(
                        coins = 32,
                        hearts = 0,
                        gems = 0
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 2,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 3,
                    image = R.drawable.background_level_3,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 3",
                    desc = Dodgers.context!!.getString(R.string.fear_of_the_dark),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.EVENING
                    ),
                    enemySet = listOf(
                        Enemy.BAT
                    ),
                    levelDuration = 30,
                    rewards = Rewards(
                        coins = 34,
                        hearts = 0,
                        gems = 0
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 3,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 3,
                    image = R.drawable.background_level_4,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 4",
                    desc = Dodgers.context!!.getString(R.string.sunset_or_sunscine),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY
                    ),
                    enemySet = listOf(
                        Enemy.ROCKS,
                        Enemy.CACTUS,
                        Enemy.BULLETS
                    ),
                    levelDuration = 35,
                    rewards = Rewards(
                        coins = 36,
                        hearts = 0,
                        gems = 0
                    ),
                    powerUpSet = mutableListOf(
                        PowerUp.SLURP_RED
                    )
                ),
                Level(
                    id = 4,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 2,
                    image = R.drawable.background_level_5,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 5",
                    desc = Dodgers.context!!.getString(R.string.out_of_town),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY
                    ),
                    enemySet = listOf(
                        Enemy.ROCKS,
                        Enemy.ROCK_UPSIDEDOWN
                    ),
                    levelDuration = 40,
                    rewards = Rewards(
                        coins = 38,
                        hearts = 0,
                        gems = 2
                    ),
                    powerUpSet = mutableListOf(
                        PowerUp.SPEED_UP
                    )
                ),
                Level(
                    id = 5,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 2,
                    image = R.drawable.background_level_6,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 6",
                    desc = Dodgers.context!!.getString(R.string.orienteering),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.SURFACE_GRASS_AND_MUSHROOMS
                    ),
                    enemySet = listOf(
                        Enemy.SAWS,
                        Enemy.COBRA
                    ),
                    levelDuration = 45,
                    rewards = Rewards(
                        coins = 40,
                        hearts = 0,
                        gems = 3
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 6,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 4,
                    image = R.drawable.background_level_7,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 7",
                    desc = Dodgers.context!!.getString(R.string.day_in_the_mountains),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.SPEED_DOWN
                    ),
                    enemySet = listOf(
                        Enemy.EAGLE,
                        Enemy.COBRA,
                        Enemy.ROCK_UPSIDEDOWN
                    ),
                    levelDuration = 45,
                    rewards = Rewards(
                        coins = 50,
                        hearts = 0,
                        gems = 4
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 7,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 6,
                    levelVariation = 4,
                    image = R.drawable.background_level_8,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 8",
                    desc = Dodgers.context!!.getString(R.string.evening_in_the_mountains),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.SURFACE_ROCK
                    ),
                    enemySet = listOf(
                        Enemy.BAT,
                        Enemy.BULLETS,
                        Enemy.COBRA
                    ),
                    levelDuration = 45,
                    rewards = Rewards(
                        coins = 50,
                        hearts = 0,
                        gems = 5
                    ),
                    powerUpSet = mutableListOf(
                        PowerUp.MEGA_JUMP
                    )
                ),
                Level(
                    id = 8,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 5,
                    levelVariation = 4,
                    image = R.drawable.background_level_9,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 9",
                    desc = Dodgers.context!!.getString(R.string.slurp_blue_effect),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.SLURP_BLUE
                    ),
                    enemySet = listOf(
                        Enemy.SAWS,
                        Enemy.BEES,
                        Enemy.ROCKS
                    ),
                    levelDuration = 45,
                    rewards = Rewards(
                        coins = 50,
                        hearts = 0,
                        gems = 6
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 9,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 1,
                    spawndelay = 5,
                    levelVariation = 4,
                    image = R.drawable.background_level_10,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 10",
                    desc = Dodgers.context!!.getString(R.string.night_in_the_mountains),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.EVENING
                    ),
                    enemySet = listOf(
                        Enemy.BAT,
                        Enemy.BUNNY
                    ),
                    levelDuration = 50,
                    rewards = Rewards(
                        coins = 60,
                        hearts = 0,
                        gems = 7
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 10,
                    levelBaseSpeed = 2,
                    levelDeltaSpeed = 1,
                    spawndelay = 5,
                    levelVariation = 4,
                    image = R.drawable.background_level_11,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 11",
                    desc = Dodgers.context!!.getString(R.string.there_is_snow),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.SNOW_BALL
                    ),
                    levelDuration = 60,
                    rewards = Rewards(
                        coins = 60,
                        hearts = 0,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 11,
                    levelBaseSpeed = 2,
                    levelDeltaSpeed = 1,
                    spawndelay = 5,
                    levelVariation = 4,
                    image = R.drawable.background_level_12,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 12",
                    desc = Dodgers.context!!.getString(R.string.ok_i_hate_the_snow),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.REVERSE
                    ),
                    enemySet = listOf(
                        Enemy.STALATTITE,
                        Enemy.SNOW_BALL
                    ),
                    levelDuration = 60,
                    rewards = Rewards(
                        coins = 60,
                        hearts = 0,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf(
                        PowerUp.DOUBLE_COINS
                    )
                ),
                Level(
                    id = 12,
                    levelBaseSpeed = 4,
                    levelDeltaSpeed = 1,
                    spawndelay = 5,
                    levelVariation = 4,
                    image = R.drawable.background_level_13,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 13",
                    desc = Dodgers.context!!.getString(R.string.do_you_like_a_bee),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.REVERSE
                    ),
                    enemySet = listOf(
                        Enemy.EGGS_RED,
                        Enemy.SAWS,
                        Enemy.BEES
                    ),
                    levelDuration = 60,
                    rewards = Rewards(
                        coins = 60,
                        hearts = 0,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 13,
                    levelBaseSpeed = 4,
                    levelDeltaSpeed = 1,
                    spawndelay = 4,
                    levelVariation = 3,
                    image = R.drawable.background_level_14,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 14",
                    desc = Dodgers.context!!.getString(R.string.is_this_the_hell),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.EGGS_RED,
                        Enemy.SAWS,
                        Enemy.BEES,
                        Enemy.BAT
                    ),
                    levelDuration = 60,
                    rewards = Rewards(
                        coins = 60,
                        hearts = 0,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 14,
                    levelBaseSpeed = 4,
                    levelDeltaSpeed = 1,
                    spawndelay = 4,
                    levelVariation = 3,
                    image = R.drawable.background_level_15,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 15",
                    desc = Dodgers.context!!.getString(R.string.cemetery),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.BAT,
                        Enemy.BROKEN_WOOD
                    ),
                    levelDuration = 70,
                    rewards = Rewards(
                        coins = 70,
                        hearts = 2,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 15,
                    levelBaseSpeed = 6,
                    levelDeltaSpeed = 1,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_16,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 16",
                    desc = Dodgers.context!!.getString(R.string.autumn),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.BRANCH_TREE_LEAF,
                        Enemy.BROKEN_WOOD,
                        Enemy.COBRA
                    ),
                    levelDuration = 70,
                    rewards = Rewards(
                        coins = 70,
                        hearts = 2,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 16,
                    levelBaseSpeed = 6,
                    levelDeltaSpeed = 1,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_17,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 17",
                    desc = Dodgers.context!!.getString(R.string.smell_of_victory),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.BRANCH_TREE,
                        Enemy.BEES
                    ),
                    levelDuration = 70,
                    rewards = Rewards(
                        coins = 70,
                        hearts = 2,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 17,
                    levelBaseSpeed = 6,
                    levelDeltaSpeed = 1,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_18,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 18",
                    desc = Dodgers.context!!.getString(R.string.relax_time),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.SURFACE_ROCK
                    ),
                    enemySet = listOf(
                        Enemy.FOX,
                        Enemy.RYNO
                    ),
                    levelDuration = 70,
                    rewards = Rewards(
                        coins = 70,
                        hearts = 2,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 18,
                    levelBaseSpeed = 6,
                    levelDeltaSpeed = 1,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_19,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 19",
                    desc = Dodgers.context!!.getString(R.string.bad_feeling),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.ALIEN_WORM
                    ),
                    levelDuration = 70,
                    rewards = Rewards(
                        coins = 70,
                        hearts = 2,
                        gems = 10
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 19,
                    levelBaseSpeed = 8,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_20,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 20",
                    desc = Dodgers.context!!.getString(R.string.bridge),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.SURFACE_DESERT
                    ),
                    enemySet = listOf(
                        Enemy.DRAGON_LITTLE
                    ),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 80,
                        hearts = 2,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 20,
                    levelBaseSpeed = 8,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_21,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 21",
                    desc = Dodgers.context!!.getString(R.string.engine_room),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED,
                        Feature.SURFACE_DESERT
                    ),
                    enemySet = listOf(
                        Enemy.DRAGON_BIG
                    ),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 80,
                        hearts = 2,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 21,
                    levelBaseSpeed = 8,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_22,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 22",
                    desc = Dodgers.context!!.getString(R.string.moon_bedroom),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.TURTLE_BIG
                    ),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 80,
                        hearts = 3,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 22,
                    levelBaseSpeed = 8,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_23,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 23",
                    desc = Dodgers.context!!.getString(R.string.sound_like_beautiful),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.ALIEN_FROG
                    ),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 80,
                        hearts = 3,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 23,
                    levelBaseSpeed = 8,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_24,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 24",
                    desc = Dodgers.context!!.getString(R.string.earth_like),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.TURTLE_BIG,
                        Enemy.DRAGON_LITTLE,
                        Enemy.DRAGON_BIG
                    ),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 80,
                        hearts = 3,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 24,
                    levelBaseSpeed = 10,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_25,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 25",
                    desc = Dodgers.context!!.getString(R.string.moon_land),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.ALIEN_BLUE_SHUTTLE
                    ),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 80,
                        hearts = 3,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 25,
                    levelBaseSpeed = 10,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_26,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 26",
                    desc = Dodgers.context!!.getString(R.string.other_side_of_the_moon),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.ALIEN_BLACK
                    ),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 90,
                        hearts = 3,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                ), Level(
                    id = 26,
                    levelBaseSpeed = 11,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_27,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 27",
                    desc = Dodgers.context!!.getString(R.string.beautiful_atmosphere),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.UFOS,
                        Enemy.TURTLE_BIG
                    ),
                    levelDuration = 82,
                    rewards = Rewards(
                        coins = 95,
                        hearts = 5,
                        gems = 15
                    ),
                    powerUpSet = mutableListOf()
                ), Level(
                    id = 27,
                    levelBaseSpeed = 11,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_28,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 28",
                    desc = Dodgers.context!!.getString(R.string.go_home),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.ALIEN_BLACK,
                        Enemy.SURFACE_LIANA
                    ),
                    levelDuration = 84,
                    rewards = Rewards(
                        coins = 95,
                        hearts = 5,
                        gems = 15
                    ),
                    powerUpSet = mutableListOf()
                ), Level(
                    id = 28,
                    levelBaseSpeed = 11,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_29,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 29",
                    desc = Dodgers.context!!.getString(R.string.not_my_home),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.DRAGON_LITTLE,
                        Enemy.BAT,
                        Enemy.ALIEN_FROG,

                    ),
                    levelDuration = 86,
                    rewards = Rewards(
                        coins = 95,
                        hearts = 5,
                        gems = 15
                    ),
                    powerUpSet = mutableListOf()
                ), Level(
                    id = 29,
                    levelBaseSpeed = 11,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.background_level_30,
                    title = Dodgers.context!!.resources.getString(R.string.level) + " 30",
                    desc = Dodgers.context!!.getString(R.string.better),
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = listOf(
                        Enemy.BULLET_AND_FLAME,
                        Enemy.SURFACE_LIANA
                    ),
                    levelDuration = 85,
                    rewards = Rewards(
                        coins = 100,
                        hearts = 5,
                        gems = 20
                    ),
                    powerUpSet = mutableListOf()
                ),
                Level(
                    id = 1000,
                    levelBaseSpeed = 1,
                    levelDeltaSpeed = 2,
                    spawndelay = 3,
                    levelVariation = 2,
                    image = R.drawable.soon,
                    title = "Soon",
                    desc = "soon",
                    featureSet = mutableListOf(
                        Feature.RUN,
                        Feature.JUMP,
                        Feature.SPAWN_COINS,
                        Feature.RANDOM_SPAWN_DELAY,
                        Feature.INCREASE_GAME_SPEED
                    ),
                    enemySet = mutableListOf(),
                    levelDuration = 80,
                    rewards = Rewards(
                        coins = 80,
                        hearts = 3,
                        gems = 12
                    ),
                    powerUpSet = mutableListOf()
                )
            )
        )
    }
}
