package com.arcadan.dodgetheenemies.util

import android.graphics.Color
import android.graphics.Paint
import com.arcadan.dodgetheenemies.graphics.Measures

class Global {
    companion object {
        var instance = Global()
    }

    var measures: Measures =
        Measures(1920, 1080)

    var debugSquarePaint: Paint = Paint().apply {
        color = Color.RED
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    var debugPointPaint: Paint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 4f
        style = Paint.Style.FILL_AND_STROKE
    }
}
