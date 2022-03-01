package com.mikai233.common.tools

import kotlin.time.Duration

class TickTimer(var tickBase: Duration, var interval: Duration) {
    private var counter: Int = 0

    fun invokeOnTimeUp(callback: () -> Unit) {
        if (tickBase >= interval) {
            callback.invoke()
        } else {
            ++counter
            val totalCount = interval / tickBase
            if (counter >= totalCount) {
                callback.invoke()
                counter = 0
            }
        }
    }
}