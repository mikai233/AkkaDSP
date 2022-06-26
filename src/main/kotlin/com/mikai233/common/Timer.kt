package com.mikai233.common

import kotlin.time.Duration

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/27
 */

class TickTimer(val base: Duration, val interval: Duration) {
    private var counter: Int = 0

    fun invokeOnTimeUp(callback: () -> Unit) {
        if (base >= interval) {
            callback.invoke()
        } else {
            ++counter
            val totalCount = interval / base
            if (counter >= totalCount) {
                callback.invoke()
                counter = 0
            }
        }
    }
}