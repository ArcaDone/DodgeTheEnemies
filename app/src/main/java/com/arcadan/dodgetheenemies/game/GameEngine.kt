package com.arcadan.dodgetheenemies.game

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.WindowManager
import com.arcadan.dodgetheenemies.R
import com.arcadan.dodgetheenemies.data.DataManager
import com.arcadan.dodgetheenemies.data.Keys
import com.arcadan.dodgetheenemies.data.Persistence
import com.arcadan.dodgetheenemies.enums.Enemy
import com.arcadan.dodgetheenemies.enums.Feature
import com.arcadan.dodgetheenemies.enums.JumpState
import com.arcadan.dodgetheenemies.enums.PowerUp
import com.arcadan.dodgetheenemies.game.objects.VectorXY
import com.arcadan.dodgetheenemies.graphics.ScrollableBackground
import com.arcadan.dodgetheenemies.models.Level
import com.arcadan.dodgetheenemies.objects.IPhysicalObject
import com.arcadan.dodgetheenemies.objects.Player
import com.arcadan.dodgetheenemies.objects.collectables.Coin
import com.arcadan.dodgetheenemies.objects.enemies.AlienBlack
import com.arcadan.dodgetheenemies.objects.enemies.AlienFrog
import com.arcadan.dodgetheenemies.objects.enemies.AlienNuke
import com.arcadan.dodgetheenemies.objects.enemies.AlienSpaceShuttle
import com.arcadan.dodgetheenemies.objects.enemies.AlienWorm
import com.arcadan.dodgetheenemies.objects.enemies.Bat
import com.arcadan.dodgetheenemies.objects.enemies.Bee
import com.arcadan.dodgetheenemies.objects.enemies.BirdPoop
import com.arcadan.dodgetheenemies.objects.enemies.BlueBird
import com.arcadan.dodgetheenemies.objects.enemies.BossBullet
import com.arcadan.dodgetheenemies.objects.enemies.BossDragon
import com.arcadan.dodgetheenemies.objects.enemies.BranchTree
import com.arcadan.dodgetheenemies.objects.enemies.BranchTreeLeaf
import com.arcadan.dodgetheenemies.objects.enemies.BrokenWood
import com.arcadan.dodgetheenemies.objects.enemies.Bullet
import com.arcadan.dodgetheenemies.objects.enemies.BulletAndFlame
import com.arcadan.dodgetheenemies.objects.enemies.Bunny
import com.arcadan.dodgetheenemies.objects.enemies.Cactus
import com.arcadan.dodgetheenemies.objects.enemies.Chicken
import com.arcadan.dodgetheenemies.objects.enemies.Cobra
import com.arcadan.dodgetheenemies.objects.enemies.DragonBig
import com.arcadan.dodgetheenemies.objects.enemies.DragonLittle
import com.arcadan.dodgetheenemies.objects.enemies.Eagle
import com.arcadan.dodgetheenemies.objects.enemies.EggsRed
import com.arcadan.dodgetheenemies.objects.enemies.Fox
import com.arcadan.dodgetheenemies.objects.enemies.Rock
import com.arcadan.dodgetheenemies.objects.enemies.RockBig
import com.arcadan.dodgetheenemies.objects.enemies.RockUpsideDown
import com.arcadan.dodgetheenemies.objects.enemies.Ryno
import com.arcadan.dodgetheenemies.objects.enemies.Saw
import com.arcadan.dodgetheenemies.objects.enemies.SnowBall
import com.arcadan.dodgetheenemies.objects.enemies.SpikyMonster
import com.arcadan.dodgetheenemies.objects.enemies.Stalattite
import com.arcadan.dodgetheenemies.objects.enemies.SurfaceLiana
import com.arcadan.dodgetheenemies.objects.enemies.TurtleBig
import com.arcadan.dodgetheenemies.objects.enemies.Ufo
import com.arcadan.dodgetheenemies.objects.powerdown.Evening
import com.arcadan.dodgetheenemies.objects.powerdown.ReverseMovement
import com.arcadan.dodgetheenemies.objects.powerdown.SlurpBlue
import com.arcadan.dodgetheenemies.objects.powerdown.SpeedDownStar
import com.arcadan.dodgetheenemies.objects.powerup.DoubleCoins
import com.arcadan.dodgetheenemies.objects.powerup.MegaJump
import com.arcadan.dodgetheenemies.objects.powerup.SlurpRed
import com.arcadan.dodgetheenemies.objects.powerup.SpeedUpStar
import com.arcadan.dodgetheenemies.objects.surfaces.SurfaceDesert
import com.arcadan.dodgetheenemies.objects.surfaces.SurfaceGrassAndMushrooms
import com.arcadan.dodgetheenemies.objects.surfaces.SurfaceIron
import com.arcadan.dodgetheenemies.objects.surfaces.SurfaceRock
import com.arcadan.dodgetheenemies.objects.surfaces.SurfaceWood
import com.arcadan.dodgetheenemies.util.Global
import com.arcadan.util.LogHelper
import com.arcadan.util.TAG
import com.google.gson.Gson
import kotlin.math.roundToLong
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("ConstantConditionIf")
class GameEngine(
    private val context: Context,
    private val holder: SurfaceHolder,
    private val level: Level,
    private val resources: Resources
) {

    private lateinit var enemyObject: Any
    private lateinit var background: Bitmap
    private lateinit var enemySprite: Bitmap
    private lateinit var backgroundBitmap: Bitmap
    private lateinit var scrollableBackground: ScrollableBackground

    lateinit var spawnJob: Job
    lateinit var spawnJob2: Job
    lateinit var spawnJob3: Job

    private var playerBitmap: Bitmap? = null
    private var nukeHasSpawned = false
    private var poopHasSpawned = false
    private var isBossInIdle = false
    private var isBossAttackTime = false
    private var spriteSelected = R.drawable.sprite_main_dodger
    private var spawnLock: Int = 0
    private var currentScore: Int = 0
    private var enemyBitmap: Bitmap? = null

    lateinit var player: Player
    lateinit var bossDragon: BossDragon
    lateinit var callBacks: GameCallBacks

    var currentCoin: Int = 0
    var currentSpeedUp: Int = 0
    var currentSlurpRed: Int = 0
    var currentMegaJump: Int = 0
    var currentDoubleCoins: Int = 0
    var shouldSpawnEvening: Boolean = true
    var shouldSpawnBlueSlurp: Boolean = true
    var shouldSpawnSpeedDown: Boolean = true

    var shouldSpawnReverseMovement: Boolean = true
    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val options: BitmapFactory.Options = BitmapFactory.Options()
    private val objects = ArrayList<IPhysicalObject>()
    private val surfaces = ArrayList<IPhysicalObject>()

    private fun spawnRandomEnemy() {
        val gen = Random.nextInt(level.enemySet.size)
        val howMuchEnemies = Random.nextInt(1, 3)
        val objectToBeGen = level.enemySet[gen]
        LogHelper.log(TAG, "Generated Number: $gen \n object: ${level.enemySet[gen]}", Log.VERBOSE)
        when (objectToBeGen) {
            Enemy.ROCKS -> {
                repeat(howMuchEnemies) {
                    spawn(Rock(resources))
                }
            }
            Enemy.BLUE_BIRDS -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_blue_bird,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = BlueBird(enemySprite)
                spawn(enemyObject as BlueBird)
            }
            Enemy.BULLETS -> {
                repeat(1) {
                    spawn(Bullet(resources))
                }
            }
            Enemy.UFOS -> {
                repeat(1) {
                    spawn(Ufo(resources))
                }
            }
            Enemy.SPIKY_MONSTERS -> {
                repeat(1) {
                    spawn(SpikyMonster(resources))
                }
            }
            Enemy.SAWS -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_saw,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = Saw(enemySprite)
                spawn(enemyObject as Saw)
            }
            Enemy.ALIEN_WORM -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_alien_worm,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = AlienWorm(enemySprite)
                spawn(enemyObject as AlienWorm)
            }
            Enemy.TURTLE_BIG -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_turtle_big,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = TurtleBig(enemySprite)
                spawn(enemyObject as TurtleBig)
            }
            Enemy.BULLET_AND_FLAME -> {
                repeat(1) {
                    spawn(BulletAndFlame(resources))
                }
            }
            Enemy.EGGS_RED -> {
                repeat(howMuchEnemies) {
                    spawn(EggsRed(resources))
                }
            }
            Enemy.BEES -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_bee,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = Bee(enemySprite)
                spawn(enemyObject as Bee)
            }
            Enemy.CACTUS -> {
                repeat(howMuchEnemies) {
                    spawn(Cactus(resources))
                }
            }
            Enemy.BRANCH_TREE -> {
                repeat(howMuchEnemies) {
                    spawn(BranchTree(resources))
                }
            }
            Enemy.BRANCH_TREE_LEAF -> {
                repeat(howMuchEnemies) {
                    spawn(BranchTreeLeaf(resources))
                }
            }
            Enemy.BROKEN_WOOD -> {
                repeat(howMuchEnemies) {
                    spawn(BrokenWood(resources))
                }
            }
            Enemy.ROCK_UPSIDEDOWN -> {
                repeat(howMuchEnemies) {
                    spawn(RockUpsideDown(resources))
                }
            }
            Enemy.ROCK_BIG -> {
                repeat(howMuchEnemies) {
                    spawn(RockBig(resources))
                }
            }
            Enemy.BAT -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_bat,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = Bat(enemySprite)
                spawn(enemyObject as Bat)
            }
            Enemy.BUNNY -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_bunny,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = Bunny(enemySprite)
                spawn(enemyObject as Bunny)
            }
            Enemy.DRAGON_LITTLE -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_little_dragon,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = DragonLittle(enemySprite)
                spawn(enemyObject as DragonLittle)
            }
            Enemy.DRAGON_BIG -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_big_dragon,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width,
                    Global.instance.measures.height / 4,
                    false
                )
                enemyObject = DragonBig(enemySprite)
                spawn(enemyObject as DragonBig)
            }
            Enemy.COBRA -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_cobra,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = Cobra(enemySprite)
                spawn(enemyObject as Cobra)
            }
            Enemy.ALIEN_BLACK -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_alien_black,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    (Global.instance.measures.width * 2) / 3,
                    (Global.instance.measures.height * 2) / 3,
                    false
                )
                enemyObject = AlienBlack(enemySprite)
                spawn(enemyObject as AlienBlack)
            }
            Enemy.ALIEN_BLUE_SHUTTLE -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_alien_2,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = AlienSpaceShuttle(enemySprite)
                spawn(enemyObject as AlienSpaceShuttle)
            }
            Enemy.ALIEN_FROG -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_alien_frog,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 2,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = AlienFrog(enemySprite)
                spawn(enemyObject as AlienFrog)
            }
            Enemy.SNOW_BALL -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_snowball,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 3,
                    Global.instance.measures.height / 6,
                    false
                )
                enemyObject = SnowBall(enemySprite)
                spawn(enemyObject as SnowBall)
            }
            Enemy.STALATTITE -> {
                repeat(howMuchEnemies) {
                    spawn(Stalattite(resources))
                }
            }
            Enemy.FOX -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_fox,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 2,
                    Global.instance.measures.height / 2,
                    false
                )
                enemyObject = Fox(enemySprite)
                spawn(enemyObject as Fox)
            }
            Enemy.RYNO -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_ryno,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 4,
                    Global.instance.measures.height / 3,
                    false
                )
                enemyObject = Ryno(enemySprite)
                spawn(enemyObject as Ryno)
            }
            Enemy.EAGLE -> {
                enemyBitmap = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.sprite_eagle,
                    options
                )
                enemySprite = Bitmap.createScaledBitmap(
                    enemyBitmap!!,
                    Global.instance.measures.width / 6,
                    Global.instance.measures.height / 4,
                    false
                )
                enemyObject = Eagle(enemySprite)
                spawn(enemyObject as Eagle)
            }
        }
        enemyBitmap?.recycle()
    }

    private fun spawnRandomFeature() {
        if (level.featureSet.contains(Feature.SPAWN_COINS)) {
            spawn(Coin(resources))
        }
        if (level.featureSet.contains(Feature.SLURP_BLUE) && shouldSpawnBlueSlurp) {
            LogHelper.log(TAG, "Spawning a Slurp blue", Log.VERBOSE)
            spawn(SlurpBlue(resources))
        }
        if (level.featureSet.contains(Feature.SPEED_DOWN) && shouldSpawnSpeedDown) {
            LogHelper.log(TAG, "Spawning a SpeedDown", Log.VERBOSE)
            spawn(SpeedDownStar(resources))
        }
        if (level.featureSet.contains(Feature.REVERSE) && shouldSpawnReverseMovement) {
            LogHelper.log(TAG, "Spawning a Reverse", Log.VERBOSE)
            spawn(ReverseMovement(resources))
        }
        if (level.featureSet.contains(Feature.EVENING) && shouldSpawnEvening) {
            LogHelper.log(TAG, "Spawning a Evening", Log.VERBOSE)
            spawn(Evening(resources))
        }
    }

    private fun spawn(phObject: IPhysicalObject) {
        synchronized(spawnLock) {
            objects.add(phObject)
        }
    }

    private fun spawnSurface(phObject: IPhysicalObject) {
        synchronized(spawnLock) {
            surfaces.add(phObject)
        }
    }

    private fun clearDiedObjects() {
        synchronized(spawnLock) {
            for (i in objects.size - 1 downTo 0) {
                if (objects[i].shouldBeCleared) {
                    if (objects[i] is BirdPoop) {
                        poopHasSpawned = false
                    }
                    if (objects[i] is AlienNuke) {
                        nukeHasSpawned = false
                    }
                    if (objects[i] is EggsRed) {
                        spawnChicken(i)
                    }
                    objects.removeAt(i)
                    currentScore++
                    callBacks.onScoreChanged(currentScore)
                    if (level.featureSet.contains(Feature.INCREASE_GAME_SPEED)) {
                        setLevelSpeed()
                    }
                }
            }
        }
    }

    private fun setLevelSpeed() {
        val totalSpeed = currentScore + level.levelBaseSpeed
        LogHelper.log(TAG, "$totalSpeed , ${level.levelBaseSpeed} , ${level.levelDeltaSpeed}")
        GameParameters.instance.deltaSpeed =
            Math.floorDiv(totalSpeed, level.levelDeltaSpeed)
    }

    private fun spawnChicken(i: Int) {
        enemyBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.sprite_chicken,
            options
        )
        enemySprite = Bitmap.createScaledBitmap(
            enemyBitmap!!,
            Global.instance.measures.width / 4,
            Global.instance.measures.height / 2,
            false
        )
        enemyObject = Chicken(enemySprite, objects[i].coordinates)
        spawn(enemyObject as Chicken)
        (enemyObject as Chicken).coordinates = objects[i].coordinates
        objects[i].coordinates.y -= 60
        enemyBitmap?.recycle()
    }

    private fun startSpawnClockEnemy() {
        spawnJob = coroutineScope.launch {
            while (!GameParameters.instance.shouldStopSpawn) {
                if (GameParameters.instance.gameRunning) {
                    LogHelper.log(TAG, "Starting an object spawn")
                    if (objects.size <= GameParameters.instance.maxObjectsOnScreen) {
                        spawnRandomEnemy()
                    }
                    val calculatedDelay = computeNextSpawnDelayEnemy()
                    LogHelper.log(TAG, "Computed enemy Spawn Delay: $calculatedDelay")
                    delay(calculatedDelay)
                }
            }
        }
    }

    private fun startSpawnClockFeature() {
        spawnJob2 = coroutineScope.launch {
            while (!GameParameters.instance.shouldStopSpawn) {
                if (GameParameters.instance.gameRunning) {
                    LogHelper.log(TAG, "Starting an object spawn")
                    if (objects.size <= GameParameters.instance.maxObjectsOnScreen) {
                        spawnRandomFeature()
                    }
                    val calculatedDelay = computeNextSpawnDelayFeature()
                    LogHelper.log(TAG, "Computed Spawn Delay: $calculatedDelay")
                    delay(calculatedDelay)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun startSpawnClockPowerUp() {
        spawnJob2 = coroutineScope.launch {
            while (!GameParameters.instance.shouldStopSpawn) {
                if (GameParameters.instance.gameRunning) {
                    LogHelper.log(TAG, "Starting an object spawn")
                    if (objects.size <= GameParameters.instance.maxObjectsOnScreen) {
                        spawnRandomPowerUp()
                    }
                    val calculatedDelay = computeNextSpawnDelayPowerUp()
                    LogHelper.log(TAG, "Computed Spawn Delay: $calculatedDelay")
                    delay(calculatedDelay)
                }
            }
        }
    }

    private fun spawnRandomPowerUp() {
        if (level.powerUpSet.size == 0)
            return
        val gen = Random.nextInt(level.powerUpSet.size)
        val objectToBeGen = level.powerUpSet[gen]
        LogHelper.log(
            TAG,
            "Generated Number: $gen \n object: ${level.powerUpSet[gen]}",
            Log.VERBOSE
        )
        when (objectToBeGen) {
            PowerUp.SLURP_RED -> {
                spawn(SlurpRed(resources))
            }
            PowerUp.SPEED_UP -> {
                spawn(SpeedUpStar(resources))
            }
            PowerUp.MEGA_JUMP -> {
                spawn(MegaJump(resources))
            }
            PowerUp.DOUBLE_COINS -> {
                spawn(DoubleCoins(resources))
            }
        }
    }

    private fun computeNextSpawnDelayEnemy(): Long {
        return if (level.featureSet.contains(Feature.RANDOM_SPAWN_DELAY)) {
            (Random.nextInt(
                level.spawndelay - level.levelVariation,
                level.spawndelay + level.levelVariation
            ) * 1000).toLong()
        } else {
            (level.spawndelay * 1000).toLong()
        }
    }

    private fun computeNextSpawnDelayFeature(): Long {
        return if (level.featureSet.contains(Feature.RANDOM_SPAWN_DELAY)) {
            (Random.nextInt(
                GameParameters.instance.spawnDelayFeature - GameParameters.instance.spawnDelayVariation,
                GameParameters.instance.spawnDelayFeature + GameParameters.instance.spawnDelayVariation
            ) * 1000).toLong()
        } else {
            (GameParameters.instance.spawnDelayFeature * 1000).toLong()
        }
    }

    private fun computeNextSpawnDelayPowerUp(): Long {
        return if (level.featureSet.contains(Feature.RANDOM_SPAWN_DELAY)) {
            (Random.nextInt(
                GameParameters.instance.spawnDelayPowerUp - GameParameters.instance.spawnDelayVariation,
                GameParameters.instance.spawnDelayPowerUp + GameParameters.instance.spawnDelayVariation
            ) * 1000).toLong()
        } else {
            (GameParameters.instance.spawnDelayPowerUp * 1000).toLong()
        }
    }

    private fun drawGame(canvas: Canvas) {
        if (level.featureSet.contains(Feature.SCROLLING)) {
            scrollableBackground.draw(canvas)
        } else {
            canvas.drawBitmap(background, null, Global.instance.measures.backgroundRect, null)
        }
        backgroundBitmap.recycle()

        player.draw(canvas)
        playerBitmap!!.recycle()

        if (level.enemySet.contains(Enemy.BOSS_DRAGON)) {
            bossDragon.draw(canvas)
            enemyBitmap?.recycle()
        }

        if (GameParameters.instance.enableDebugMode) {
            canvas.drawRect(
                player.hitbox,
                Global.instance.debugSquarePaint
            )
            canvas.drawCircle(
                player.coordinates.x.toFloat(),
                player.coordinates.y.toFloat(),
                2f,
                Global.instance.debugPointPaint
            )
        }

        synchronized(spawnLock) {
            for (gameObject in surfaces) {
                gameObject.draw(canvas)
                if (GameParameters.instance.enableDebugMode) {
                    canvas.drawRect(gameObject.hitbox, Global.instance.debugSquarePaint)
                    canvas.drawCircle(
                        gameObject.coordinates.x.toFloat(),
                        gameObject.coordinates.y.toFloat(),
                        2f,
                        Global.instance.debugPointPaint
                    )
                }
            }
            for (gameObject in objects) {
                gameObject.draw(canvas)
                if (GameParameters.instance.enableDebugMode) {
                    canvas.drawRect(gameObject.hitbox, Global.instance.debugSquarePaint)
                    canvas.drawCircle(
                        gameObject.coordinates.x.toFloat(),
                        gameObject.coordinates.y.toFloat(),
                        2f,
                        Global.instance.debugPointPaint
                    )
                }
            }
            if (level.enemySet.contains(Enemy.BOSS_DRAGON) && GameParameters.instance.enableDebugMode) {
                canvas.drawRect(bossDragon.hitbox, Global.instance.debugSquarePaint)
                canvas.drawCircle(
                    bossDragon.coordinates.x.toFloat(),
                    bossDragon.coordinates.y.toFloat(),
                    2f,
                    Global.instance.debugPointPaint
                )
            }
        }
    }

    private fun startGame() {
        objects.clear()
        GameParameters.instance.shouldStopSpawn = false
        setSkinSelected()
        if (level.featureSet.contains(Feature.SCROLLING)) {
            backgroundBitmap = BitmapFactory.decodeResource(
                resources,
                level.image,
                options
            )
            scrollableBackground =
                ScrollableBackground(
                    Bitmap.createScaledBitmap(
                        backgroundBitmap,
                        Global.instance.measures.width,
                        Global.instance.measures.height, false
                    ),
                    Global.instance.measures.backgroundRect
                )
        } else {
            backgroundBitmap = BitmapFactory.decodeResource(
                resources,
                level.image,
                options
            )
            background = Bitmap.createScaledBitmap(
                backgroundBitmap,
                Global.instance.measures.width,
                Global.instance.measures.height,
                false
            )
        }
        applyBackpack()
        GameParameters.instance.resetParameters()
        startSpawnClockEnemy()
        startSpawnClockFeature()
        startSpawnClockPowerUp()
        GameParameters.instance.gameRunning = true
        setLevelSpeed()

        if (level.enemySet.contains(Enemy.BOSS_DRAGON)) {
            enemyBitmap = BitmapFactory.decodeResource(
                resources,
                R.drawable.sprite_boss,
                options
            )
            val bossWalkSprite = Bitmap.createScaledBitmap(
                enemyBitmap!!,
                Global.instance.measures.width,
                Global.instance.measures.height,
                false
            )
            bossDragon = BossDragon(
                sprite = bossWalkSprite
            )
            bossDragon.coordinates.y =
                bossDragon.coordinates.y - 180 // player coordinates Y was under ground
        }

        repeat(3) {
            chooseSurface()
        }
        if (level.enemySet.contains(Enemy.SURFACE_LIANA))
            spawnSurface(SurfaceLiana(resources))
    }

    private fun setSkinSelected() {
        spriteSelected =
            when (Persistence.instance.getInt(Keys.SELECTED_SKIN)) {
                1 -> R.drawable.sprite_gaek_tattoo_ink
                2 -> R.drawable.sprite_tentazioni_chef
                3 -> R.drawable.character_jumper
                4 -> R.drawable.sprite_old_main_dodger
                5 -> R.drawable.sprite_mummy
                6 -> R.drawable.sprite_skeleton
                7 -> R.drawable.sprite_snowman
                8 -> R.drawable.sprite_santa_clous
                9 -> R.drawable.sprite_glove_winter
                else -> R.drawable.sprite_main_dodger
            }
    }

    private fun chooseSurface() {
        val featureList = level.featureSet
        when {
            featureList.contains(Feature.SURFACE_DESERT) -> {
                spawnSurface(SurfaceDesert(resources))
            }
            featureList.contains(Feature.SURFACE_GRASS_AND_MUSHROOMS) -> {
                spawnSurface(SurfaceGrassAndMushrooms(resources))
            }
            featureList.contains(Feature.SURFACE_WOOD) -> {
                spawnSurface(SurfaceWood(resources))
            }
            featureList.contains(Feature.SURFACE_IRON) -> {
                spawnSurface(SurfaceIron(resources))
            }
            featureList.contains(Feature.SURFACE_ROCK) -> {
                spawnSurface(SurfaceRock(resources))
            }
        }
    }

    private fun applyBackpack() {
        if (GameParameters.instance.backpack.contains(PowerUp.SLURP_RED)) {
            setPlayerScale(Player.defaultSize.width / 2, Player.defaultSize.height / 2)
        } else {
            setPlayerScale(Player.defaultSize.width, Player.defaultSize.height)
        }

        player.coordinates.y = player.coordinates.y - 60 // player coordinates Y was under ground

        if (GameParameters.instance.backpack.contains(PowerUp.SPEED_UP)) {
            player.speed = 5
        }
        if (GameParameters.instance.backpack.contains(PowerUp.MEGA_JUMP)) {
            GameParameters.instance.jumpEquationC = GameParameters.instance.megaJumpValue
        }
        if (GameParameters.instance.backpack.contains(PowerUp.DOUBLE_COINS)) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(300)
                callBacks.setDoubleCoinImage()
            }
        }
    }

    private fun finishGame() {
        GameParameters.instance.shouldStopSpawn = true

        GameParameters.instance.backpack.clear()
        GameParameters.instance.jumpGravity = 1

        GameParameters.instance.jumpEquationC = (Global.instance.measures.height / 10).toDouble()

        GameParameters.instance.gameRunning = false
        stopObjectsAnimation()
        player.stopAnimation()
        val score: Int = currentScore
        val coins: Int = currentCoin

        if (GameParameters.instance.hasWon) {
            unlockLevel()
            storeExperience()
            addNewCoins(coins)
            addNewConsumables()
            addHeartsReward()
            callBacks.onGameWon(
                coins,
                score,
                Gson().toJson(level)
            )
        } else {
            callBacks.onGameOver(
                coins,
                score,
                Gson().toJson(level)
            )
        }
    }

    private fun addNewCoins(coins: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                DataManager.instance.user.value!!.coins += coins + level.rewards.coins
            } catch (e: Exception) {
                LogHelper.log(TAG, "User Observer Exception: ${e.stackTrace}")
            }
        }
    }

    private fun addNewConsumables() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                repeat(currentSlurpRed) {
                    DataManager.instance.user.value!!.consumables[PowerUp.SLURP_RED.ordinal].quantity += 1
                }
                repeat(currentSpeedUp) {
                    DataManager.instance.user.value!!.consumables[PowerUp.SPEED_UP.ordinal].quantity += 1
                }
                repeat(currentMegaJump) {
                    DataManager.instance.user.value!!.consumables[PowerUp.MEGA_JUMP.ordinal].quantity += 1
                }
                repeat(currentDoubleCoins) {
                    DataManager.instance.user.value!!.consumables[PowerUp.DOUBLE_COINS.ordinal].quantity += 1
                }
            } catch (e: Exception) {
                LogHelper.log(TAG, "User Observer Exception: ${e.stackTrace}")
            }
        }
    }

    private fun unlockLevel() {
        val levelSelected = Persistence.instance.getInt(Keys.SELECTED_LEVEL)
        if (levelSelected >= DataManager.instance.user.value!!.unlockedLevel &&
            DataManager.instance.user.value!!.unlockedLevel < DataManager.instance.levels.size - 2 // levels.size - 1 is Coming Soon
        ) {
            DataManager.instance.user.value!!.unlockedLevel += 1
        }
    }

    private fun addHeartsReward() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                DataManager.instance.user.value!!.hearts += level.rewards.hearts
            } catch (e: Exception) {
                LogHelper.log(TAG, "User Observer Exception: ${e.stackTrace}")
            }
        }
    }

    private fun storeExperience() {
        DataManager.instance.user.value!!.experience += currentScore
        manageExpLevel()
    }

    private fun manageExpLevel() {
        if (DataManager.instance.user.value!!.experience > DataManager.instance.user.value!!.nextExpLevel) {
            setPlayerExp()
            setNextLevel()
            setNewLevelPlayer()
        }
    }

    private fun setPlayerExp() {
        DataManager.instance.user.value!!.experience -= DataManager.instance.user.value!!.nextExpLevel.toInt()
    }

    private fun setNextLevel() {
        DataManager.instance.user.value!!.nextExpLevel =
            (DataManager.instance.user.value!!.nextExpLevel * GameParameters.instance.nextLevelGap).roundToLong()
    }

    private fun setNewLevelPlayer() {
        DataManager.instance.user.value!!.levelPlayer += GameParameters.instance.newLevel
        Persistence.instance.saveBool(Keys.NEW_LEVEL_ACHIEVED, true)
    }

    fun onSensorChanged(event: SensorEvent) {
        if (
            GameParameters.instance.gameRunning &&
            event.sensor.type == Sensor.TYPE_ACCELEROMETER &&
            level.featureSet.contains(Feature.RUN)
        ) {
            val rotation: Int =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
            val useValue: Float = if (rotation == 0) -event.values[0] else event.values[1]
            LogHelper.log(TAG, "Device Rotation: $useValue", Log.VERBOSE)
            when {
                useValue < -.3 -> {
                    movePlayerLeft()
                }
                useValue > .3 -> {
                    movePlayerRight()
                }
                else -> player.stopAnimation()
            }
        }
    }

    fun movePlayerRight() {
        if (GameParameters.instance.reverseDirection) {
            player.startAnimation()
            player.mustMirrorImage(true)
            player.moveLeft()
        } else {
            player.startAnimation()
            player.mustMirrorImage(false)
            player.moveRight()
        }
    }

    fun movePlayerLeft() {
        if (GameParameters.instance.reverseDirection) {
            player.startAnimation()
            player.mustMirrorImage(false)
            player.moveRight()
        } else {
            player.startAnimation()
            player.mustMirrorImage(true)
            player.moveLeft()
        }
    }

    private fun moveBossRight() {
        bossDragon.startAnimation()
        bossDragon.mustMirrorImage(false)
        bossDragon.moveRight()
    }

    private fun moveBossLeft() {
        bossDragon.startAnimation()
        bossDragon.moveLeft()
    }

    fun onTouchEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN &&
            level.featureSet.contains(Feature.JUMP)
        ) {
            player.jump()
        }
    }

    fun update() {
        if (GameParameters.instance.gameRunning) {
            startObjectsAnimation()
            var gameSpeed: Int = currentScore / 2
            if (gameSpeed < 4) gameSpeed = 4
            if (level.featureSet.contains(Feature.SCROLLING)) {
                scrollableBackground.update(gameSpeed)
            }
            clearDiedObjects()

            if (level.enemySet.contains(Enemy.BOSS_DRAGON)) {
                val testValue =
                    player.coordinates.x + Global.instance.measures.width / 4 - bossDragon.coordinates.x
                when {
                    testValue < -30 -> {
                        if (!isBossAttackTime) {
                            if (isBossInIdle) {
                                isBossInIdle = false
                                bossWalkAnimation()
                            }
                            moveBossLeft()
                        }
                    }
                    testValue > 70 -> {
                        isBossAttackTime = false
//                        if(!isBossAttackTime) {
                        if (isBossInIdle) {
                            isBossInIdle = false
                            bossWalkAnimation()
                        }
                        moveBossRight()
//                        }
                    }
                    else -> {
                        bossDragon.stopAnimation()
                        if (!isBossInIdle) {
                            isBossInIdle = true
                            isBossAttackTime = true
//                            bossIdleAnimation()
                            startBossAttackTimer()
                        }
                    }
                }
            }

            // This is basically how gravity works
            if (player.jumpState != JumpState.RISING) {
                if (isObjectOnASurface(player)) {
                    player.shouldFall = false
                    for (i in surfaces.size - 1 downTo 0) {
                        if (surfaces[i] is SurfaceLiana && isObjectOnANotGroundSurface(player)) {
                            finishGame()
                        }
                    }
                } else {
                    player.shouldFall = true
                }
            }
            player.fall()

            synchronized(spawnLock) {
                for (i in objects.size - 1 downTo 0) {
                    objects[i].update()
                    if (player.coordinates.x + Global.instance.measures.width / 4 > (objects[i]).coordinates.x && objects[i] is BlueBird && !poopHasSpawned) {
                        spawn(BirdPoop(resources, objects[i].coordinates))
                        poopHasSpawned = true
                    }
                    if (player.coordinates.x + Global.instance.measures.width / 3 > (objects[i]).coordinates.x && objects[i] is AlienSpaceShuttle && !nukeHasSpawned) {
                        spawn(AlienNuke(resources, objects[i].coordinates))
                        nukeHasSpawned = true
                    }
                    if (shouldObjectFall(objects[i])) {
                        objects[i].fall()
                    }
                    handlePlayerIntersections(i)
                }
            }
        } else {
            stopObjectsAnimation()
        }
        if (GameParameters.instance.hasWon) {
            finishGame()
            GameParameters.instance.hasWon = false
        }
    }

    private fun stopObjectsAnimation() {
        for (i in objects.size - 1 downTo 0) {
            objects[i].stopAnimation()
        }
    }

    private fun startObjectsAnimation() {
        for (i in objects.size - 1 downTo 0) {
            objects[i].startAnimation()
        }
    }

    private fun bossWalkAnimation() {
        val bossXY: VectorXY = bossDragon.coordinates
        enemyBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.sprite_boss,
            options
        )
        val bossWalkSprite = Bitmap.createScaledBitmap(
            enemyBitmap!!,
            Global.instance.measures.width,
            Global.instance.measures.height,
            false
        )
        bossDragon = BossDragon(
            sprite = bossWalkSprite
        )
        bossDragon.coordinates = bossXY
        bossDragon.initAnimator(bossWalkSprite)
        bossDragon.startAnimation()
    }

//    private fun bossIdleAnimation() {
//        val bossXY: VectorXY = bossDragon.coordinates
//        val spriteIdleSprite = Bitmap.createScaledBitmap(
//            BitmapFactory.decodeResource(
//                resources,
//                R.drawable.sprite_boss_idle,
//                options
//            ),
//            Global.instance.measures.width,
//            Global.instance.measures.height,
//            false
//        )
//        bossDragon = BossDragon(
//            sprite = spriteIdleSprite
//        )
//        bossDragon.coordinates = bossXY
//        bossDragon.initAnimator(spriteIdleSprite)
//        bossDragon.startAnimation()
//    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun startBossAttackTimer() {
        spawnJob3 = coroutineScope.launch {
            while (isBossAttackTime) {
                if (GameParameters.instance.gameRunning) {
                    if (objects.size <= GameParameters.instance.maxObjectsOnScreen) {
                        spawnBossBullet()
                        bossAttackAnimation()
                    }
                }
                delay(1500)
            }
        }
    }

    private fun bossAttackAnimation() {
        val bossXY: VectorXY = bossDragon.coordinates
        enemyBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.sprite_boss_attack,
            options
        )
        val spriteAttackSprite = Bitmap.createScaledBitmap(
            enemyBitmap!!,
            Global.instance.measures.width,
            Global.instance.measures.height,
            false
        )
        bossDragon = BossDragon(
            sprite = spriteAttackSprite
        )
        bossDragon.coordinates = bossXY
        bossDragon.initAnimator(spriteAttackSprite)
        bossDragon.startAnimation()
    }

    private fun spawnBossBullet() {
        val bossBulletY = player.coordinates.y + 30
        val bossBulletX = bossDragon.coordinates.x
        val coordinatesXY = VectorXY(bossBulletX, bossBulletY)
        enemyBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.sprite_boss_bullet,
            options
        )
        enemySprite = Bitmap.createScaledBitmap(
            enemyBitmap!!,
            Global.instance.measures.width / 8,
            Global.instance.measures.height / 8,
            false
        )
        enemyObject = BossBullet(enemySprite, coordinatesXY)
        spawn(enemyObject as BossBullet)
        (enemyObject as BossBullet).coordinates = coordinatesXY
    }

    private fun shouldObjectFall(gameObject: IPhysicalObject) =
        !(isObjectOnASurface(gameObject) && gameObject.shouldStopOnSurfaces)

    fun draw() {
        val canvas: Canvas? = holder.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(Color.WHITE)
            this.drawGame(canvas)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun handlePlayerIntersections(objectIndex: Int) {
        if (objects.size > 0 && player.hitbox.intersect(objects[objectIndex].hitbox) && !GameParameters.instance.enableDebugMode) {
            if (objects[objectIndex].isLethal) {
                finishGame()
            } else {
                objects[objectIndex].runIntersectBehaviour(this)
                objects.removeAt(objectIndex)
            }
        }
    }

    fun setPlayerScale(scaleWidth: Int, scaleHeight: Int) {
        playerBitmap = BitmapFactory.decodeResource(
            resources,
            spriteSelected,
            options
        )
        player = Player(
            sprite = Bitmap.createScaledBitmap(
                playerBitmap!!,
                scaleWidth,
                scaleHeight,
                false
            )
        )
    }

    private fun isObjectOnASurface(gameObject: IPhysicalObject): Boolean {
        if (gameObject.hitbox.bottom > Global.instance.measures.groundY) {
            return true
        } else {
            if (isObjectOnANotGroundSurface(gameObject)) return true
        }
        return false
    }

    private fun isObjectOnANotGroundSurface(gameObject: IPhysicalObject): Boolean {
        for (s in surfaces) {
            if (gameObject.hitbox.bottom >= s.hitbox.top &&
                gameObject.hitbox.bottom <= (s.hitbox.top + GameParameters.instance.standingOnASurfaceTolerance) &&
                gameObject.hitbox.left < s.hitbox.right &&
                gameObject.hitbox.right > s.hitbox.left
            ) {
                return true
            }
        }
        return false
    }

    init {
        options.inScaled = false
        startGame()
    }
}

interface GameCallBacks {
    fun onGameOver(coins: Int, score: Int, toJson: String)
    fun onGameWon(coins: Int, score: Int, toJson: String)
    fun onCoinsChanged(coins: Int)
    fun onScoreChanged(score: Int)
    fun onEveningHit()
    fun onSpeedDownHit()
    fun onReverseMovementHit()
    fun onSlurpBlueHit()
    fun setDoubleCoinImage()
}
