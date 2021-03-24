package com.arcadan.dodgetheenemies

import android.app.Application
import android.content.Context

class Dodgers : Application() {
    companion object {
        var context: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}
