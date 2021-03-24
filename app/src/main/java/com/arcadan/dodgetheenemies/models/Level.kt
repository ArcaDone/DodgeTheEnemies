package com.arcadan.dodgetheenemies.models

import com.arcadan.dodgetheenemies.enums.Enemy
import com.arcadan.dodgetheenemies.enums.Feature
import com.arcadan.dodgetheenemies.enums.PowerUp

data class Level(
    var id: Int,
    var image: Int,
    var title: String,
    var desc: String,
    var featureSet: MutableList<Feature>,
    var powerUpSet: MutableList<PowerUp>,
    var enemySet: List<Enemy>,
    var levelDuration: Int,
    var levelDeltaSpeed: Int,
    var levelBaseSpeed: Int,
    var spawndelay: Int,
    var levelVariation: Int,
    var rewards: Rewards
)
