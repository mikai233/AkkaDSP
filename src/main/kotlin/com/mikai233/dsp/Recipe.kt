package com.mikai233.dsp

import com.mikai233.dsp.item.Item
import kotlin.time.Duration

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */
data class Recipe(
    val id: Int,
    val name: String,
    val consume: Set<Item>,
    val produce: Set<Item>,
    val speed: Duration,
    val powerUsage: Double,
)
