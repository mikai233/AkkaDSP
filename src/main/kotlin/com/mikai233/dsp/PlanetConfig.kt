package com.mikai233.dsp

import com.mikai233.dsp.item.Item

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */
data class PlanetConfig(
    val name: String = "",
    val type: PlanetType = PlanetType.NORMAL,
    val coordinate: Coordinate = Coordinate.ZERO,
    val radius: Double = 0.0,
    val resources: MutableMap<Coordinate, Item> = mutableMapOf(),
)
