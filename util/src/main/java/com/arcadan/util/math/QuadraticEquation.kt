package com.arcadan.util.math

import java.lang.Math.*
import kotlin.math.pow

data class QuadraticEquation(val a: Double, val b: Double, val c: Double) {
    data class Complex(val r: Double, val i: Double) {
        override fun toString() = when {
            i == 0.0 -> r.toString()
            r == 0.0 -> "${i}i"
            else -> "$r + ${i}i"
        }
    }

    data class Solution(val x1: Any, val x2: Any) {
        override fun toString() = when(x1) {
            x2 -> "X1,2 = $x1"
            else -> "X1 = $x1, X2 = $x2"
        }
    }

    val quadraticRoots by lazy {
        val _2a = a + a
        val d = b * b - 4.0 * a * c  // discriminant
        if (d < 0.0) {
            val r = -b / _2a
            val i = kotlin.math.sqrt(-d) / _2a
            Solution(Complex(r, i), Complex(r, -i))
        } else {
            // avoid calculating -b +/- sqrt(d), to avoid any
            // subtractive cancellation when it is near zero.
            val r = if (b < 0.0) (-b + kotlin.math.sqrt(d)) / _2a else (-b - kotlin.math.sqrt(d)) / _2a
            Solution(r, c / (a * r))
        }
    }

    fun obtainYForXValue(x: Double) : Double = ((x.pow(2) * a) + (x * b) + c)
}
