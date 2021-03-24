package com.arcadan.dodgetheenemies.game

class GameThread(val gameEngine: GameEngine) : Thread() {
    @Volatile
    var running = true
        private set
    private val frameRate: Int = 1000

    override fun run() {
        var lastTime: Long = System.currentTimeMillis()

        // Method to create a game loop
        while (running) {
            val now: Long = System.currentTimeMillis()
            val elapsed: Long = now - lastTime

            if (elapsed < frameRate) {
                gameEngine.update()
                gameEngine.draw()
            }

            lastTime = now
        }
    }

    fun shutdown() {
        running = false
    }
}
