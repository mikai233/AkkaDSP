package com.mikai233.actor

import akka.actor.AbstractActorWithTimers
import com.mikai233.common.actorLogger
import com.mikai233.common.logPostStop
import com.mikai233.common.logPreStart
import com.mikai233.dsp.PlanetConfig

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */

class PlanetActor(val config: PlanetConfig = PlanetConfig()) : AbstractActorWithTimers() {
    private val logger = actorLogger()

    override fun createReceive(): Receive {
        return receiveBuilder().build()
    }

    override fun preStart() {
        logPreStart(logger)
    }

    override fun postStop() {
        logPostStop(logger)
    }
}