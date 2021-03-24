package com.arcadan.dodgetheenemies.game

import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.util.Global

class GameParameters {
    companion object {
        var instance = GameParameters()
    }

    var gameRunning: Boolean = false
    var shouldStopSpawn: Boolean = false

    // ========== Variable Parameters ===========
    var hasWon: Boolean = false

    // Experience
    var nextLevelGap: Double = 1.25

    // Movement
    var reverseDirection: Boolean = false
    var deltaSpeed: Int = 0

    // Backpack
    var backpack: MutableList<PowerUp> = mutableListOf()

    // Jump
    var jumpEquationC: Double = (Global.instance.measures.height / 10).toDouble() // Real jump height in pixels
    var jumpGravity: Int = 1 // Millis between each position update during jump

    fun resetParameters() {
        deltaSpeed = 0
    }

    // ========== Constant Parameters ===========
    // Debug
    val enableDebugMode: Boolean = false

    // Experience
    val levelFull = 10000
    val newLevel = 1

    // Jump
    val jumpEquationA: Double = -.01 // Jump width (should be left at this value, this regulates how much time the player spends in air)
    val jumpEquationB: Double = 0.0 // Leave this to 0
    val megaJumpValue: Double = (Global.instance.measures.height / 4).toDouble()

    // Movement
    val playerSpeed: Int = 12 // Pixels per rotation detection

    // Spawn
    val spawnDelayFeature: Int = 5 // Seconds
    val spawnDelayPowerUp: Int = 7 // Seconds
    val spawnDelayVariation = 1 // + or - Seconds relative to spawnDelay
    val maxObjectsOnScreen: Int = 20 // Count

    // Physics
    val fallSpeed: Int = 20 // Pixels per frame_green
    val standingOnASurfaceTolerance = 20 // Pixels

    // Hearts
    val deltaHearts: Int = 5
    val maxHearts: Int = 100
    val restoreHeartsRate: Int = 2

    // Shop prices
    // Power Up
    val slurpRedPrice: Int = 250
    val speedUpPrice: Int = 200
    val megaJumpPrice: Int = 150
    val doubleCoinsPrice: Int = 50

    // Coins
    val littleCoinPrice: Int = 30
    val mediumCoinPrice: Int = 50
    val largeCoinPrice: Int = 80
    val littleChestPrice: Int = 100
    val mediumChestPrice: Int = 160
    val largeChestPrice: Int = 200

    // Hearts
    val tenHeartsPrice: Int = 0
    val twentyHeartsPrice: Int = 300
    val fortyHeartsPrice: Int = 500
    val sixtyHeartsPrice: Int = 650
    val eightyHeartsPrice: Int = 850
    val oneHundredHeartsPrice: Int = 1200

    // Gems
    val firstGemPrice: Int = 0
    val secondGemPrice: Int = 0

    // Skins
    val firstSkinPrice: Int = 0
    val secondSkinPrice: Int = 0

    // Shop rewards
    // Power Up
    val slurpRedReward: Int = 1
    val speedUpReward: Int = 1
    val megaJumpReward: Int = 1
    val doubleCoinsReward: Int = 1

    // Coins
    val oneHundredCoinsReward: Int = 100
    val littleCoinsReward: Int = 230
    val mediumCoinsReward: Int = 500
    val largeCoinsReward: Int = 760
    val littleCoinsChestReward: Int = 1000
    val mediumCoinsChestReward: Int = 2000
    val largeCoinsChestReward: Int = 3000

    // Hearts
    val twentyHeartsReward = 20
    val fortyHeartsReward = 40
    val sixtyHeartsReward = 60
    val eightyHeartsReward = 80
    val oneHundredHearReward = 100

    // Gems
    val eightyGemReward: Int = 80
    val fiveHundredGemReward: Int = 500
    val oneThousandTwoHundredGemsReward: Int = 1200
    val twoThousandFiveHundredGemReward: Int = 2500
    val sixThousandFiveHundredGemReward: Int = 6500
    val fourteenThousandGemsReward: Int = 14000

    // Sku
    val littleGemsSku = "little_gems"
    val mediumGemsSku = "medium_gems"
    val largeGemsSku = "large_gems"
    val littleGemsChestSku = "little_gem_chest"
    val mediumGemsChestSku = "medium_gem_chest"
    val largeGemsChestSku = "large_gem_chest"
}
