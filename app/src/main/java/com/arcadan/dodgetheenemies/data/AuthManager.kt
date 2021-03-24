package com.arcadan.dodgetheenemies.data

import android.util.Log
import com.arcadan.dodgetheenemies.data.models.Consumable
import com.arcadan.dodgetheenemies.data.models.HeartsCount
import com.arcadan.dodgetheenemies.data.models.User
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.util.BuildConfig
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import kotlin.random.Random
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthManager {
    companion object {

        private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            LogHelper.log(TAG, "User Observer Exception: ${exception.stackTrace}", Log.ERROR)
        }

        fun generateUser() {
            CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
                val gen = Random.nextInt(10000000, 99999999)
                if (DatabaseManager.getUserMapValue(checkIfDebugUser(gen)).isEmpty()) {
                    DatabaseManager.addUser(
                        gen,
                        User(
                            coins = 800,
                            experience = 0,
                            nextLevel = 20,
                            levelPlayer = 1,
                            hearts = 150,
                            gems = 250,
                            unlockedLevel = 0,
                            heartsCount = HeartsCount(
                                System.currentTimeMillis() / 1000,
                                false,
                                0,
                                System.currentTimeMillis() / 1000
                            ),
                            consumables = addList()
                        )
                    )
                    Persistence.instance.saveInt(Keys.USER_ID, gen)
                } else {
                    LogHelper.log(TAG, "Should call again", Log.ASSERT)
                    generateUser()
                }
            }
        }

        private fun checkIfDebugUser(gen: Int) =
            if (BuildConfig.DEBUG)
                "debugUsers/$gen"
            else
                "users/$gen"

        private fun addList(): MutableList<Consumable> {
            val initialList = mutableListOf<Consumable>()
            initialList.addAll(
                listOf(
                    Consumable(
                        PowerUp.SLURP_RED.name,
                        0
                    ),
                    Consumable(
                        PowerUp.SPEED_UP.name,
                        0
                    ),
                    Consumable(
                        PowerUp.MEGA_JUMP.name,
                        0
                    ),
                    Consumable(
                        PowerUp.DOUBLE_COINS.name,
                        0
                    )
                )
            )
            return initialList
        }
    }
}
