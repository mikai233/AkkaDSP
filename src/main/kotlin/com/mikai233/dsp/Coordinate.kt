package com.mikai233.dsp

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */
data class Coordinate(val x: Double, val y: Double, val z: Double) {
    companion object {
        val ZERO
            get() = Coordinate(0.0, 0.0, 0.0)
    }

    fun distance(other: Coordinate): Double {
        return sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))
    }
}
