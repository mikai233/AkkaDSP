package com.mikai233.common.coroutine

import akka.actor.NotInfluenceReceiveTimeout

/**
 * @author mikai233
 * @date 2022/2/15
 */

data class NamedRunnable(val name: String, private val block: () -> Unit) : Runnable, NotInfluenceReceiveTimeout {
    override fun run() {
        block.invoke()
    }
}