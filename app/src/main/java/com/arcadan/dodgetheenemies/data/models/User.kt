package com.arcadan.dodgetheenemies.data.models

import com.arcadan.dodgetheenemies.data.DatabaseManager
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User(
    gems: Int,
    coins: Int,
    hearts: Int,
    experience: Int,
    nextLevel: Long,
    levelPlayer: Int,
    unlockedLevel: Int,
    heartsCount: HeartsCount,
    consumables: MutableList<Consumable>
) {
    private var _gems = gems
    var gems: Int
        get() = _gems
        set(value) {
            _gems = value
            DatabaseManager.setValue("gems", _gems)
        }

    private var _coins = coins
    var coins: Int
        get() = _coins
        set(value) {
            _coins = value
            DatabaseManager.setValue("coins", _coins)
        }

    private var _hearts = hearts
    var hearts: Int
        get() = _hearts
        set(value) {
            _hearts = value
            DatabaseManager.setValue("hearts", _hearts)
        }

    private var _experience = experience
    var experience: Int
        get() = _experience
        set(value) {
            _experience = value
            DatabaseManager.setValue("experience", _experience)
        }

    private var _nextExpLevel = nextLevel
    var nextExpLevel: Long
        get() = _nextExpLevel
        set(value) {
            _nextExpLevel = value
            DatabaseManager.setValue("nextExpLevel", _nextExpLevel)
        }

    private var _levelPlayer = levelPlayer
    var levelPlayer: Int
        get() = _levelPlayer
        set(value) {
            _levelPlayer = value
            DatabaseManager.setValue("levelPlayer", _levelPlayer)
        }

    private var _unlockedLevel = unlockedLevel
    var unlockedLevel: Int
        get() = _unlockedLevel
        set(value) {
            _unlockedLevel = value
            DatabaseManager.setValue("unlockedLevel", _unlockedLevel)
        }

    private var _heartsCount = heartsCount
    var heartsCount: HeartsCount
        get() = _heartsCount
        set(value) {
            _heartsCount = value
            DatabaseManager.setValue("heartsCount", _heartsCount)
        }

    private var _consumables = consumables
    var consumables: MutableList<Consumable>
        get() = _consumables
        set(value) {
            _consumables = value
            DatabaseManager.setValue("consumables", _consumables)
        }

    fun toHashMap(): HashMap<*, *> {
        val map = hashMapOf<String, Any>()
        map["coins"] = coins
        map["experience"] = experience
        map["nextExpLevel"] = nextExpLevel
        map["levelPlayer"] = levelPlayer
        map["hearts"] = hearts
        map["gems"] = gems
        map["unlockedLevel"] = unlockedLevel
        val hcMap = hashMapOf<String, Any>()
        hcMap["localDateTime"] = heartsCount.localDateTime
        hcMap["timerHasStarted"] = heartsCount.timerHasStarted
        hcMap["counter"] = heartsCount.counter
        hcMap["daysCount"] = heartsCount.daysCount
        map["heartsCount"] = hcMap
        val cMap = hashMapOf<String, Any>()
//        cMap["slurpRed"] = consumables.slurpRed
//        cMap["speedUp"] = consumables.speedUp
//        cMap["megaJump"] = consumables.megaJump
//        cMap["doubleCoins"] = consumables.doubleCoins
        map["consumables"] = cMap
        return map
    }

    companion object {
        fun fromHashMap(map: HashMap<*, *>): User {
            return User(
                coins = (map["coins"] as Long).toInt(),
                experience = (map["experience"] as Long).toInt(),
                nextLevel = (map["nextExpLevel"] as Long),
                levelPlayer = (map["levelPlayer"] as Long).toInt(),
                hearts = (map["hearts"] as Long).toInt(),
                gems = (map["gems"] as Long).toInt(),
                unlockedLevel = (map["unlockedLevel"] as Long).toInt(),
                heartsCount = HeartsCount(
                    localDateTime = (map["heartsCount"] as HashMap<*, *>)["localDateTime"] as Long,
                    timerHasStarted = (map["heartsCount"] as HashMap<*, *>)["timerHasStarted"] == true,
                    counter = ((map["heartsCount"] as HashMap<*, *>)["counter"] as Long).toInt(),
                    daysCount = (map["heartsCount"] as HashMap<*, *>)["daysCount"] as Long
                ),
                consumables = if (map["consumables"] != null) {
                    consumableManager(map)
                } else {
                    mutableListOf()
                }
            )
        }

        private fun consumableManager(map: HashMap<*, *>): MutableList<Consumable> {
            val arrayList = map["consumables"] as ArrayList<*>
            val tempList = mutableListOf<Consumable>()
            for (i in 0 until arrayList.size) {
                tempList.add(Consumable.fromHashMap(arrayList[i] as HashMap<*, *>))
            }
            return tempList
        }
    }
}
