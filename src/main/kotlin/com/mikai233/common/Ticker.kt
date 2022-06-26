package com.mikai233.common

import kotlin.time.Duration

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/27
 */
interface Ticker {
    val base: Duration
    fun tick()
}