package com.mikai233.common.coroutine

import akka.actor.Props
import akka.actor.UntypedAbstractActor
import akka.event.Logging

/**
 * @author mikai233
 * @date 2022/2/15
 */

class WorkerActor(val name: String) : UntypedAbstractActor() {
    companion object {
        fun props(name: String): Props = Props.create(WorkerActor::class.java, name)
    }

    override fun preStart() {
        logger.info("WorkerActor:{} {}, preStart", name, self)
    }

    override fun postStop() {
        logger.info("WorkerActor:{} {}, postStop", name, self)
    }

    private val logger = Logging.getLogger(context.system, javaClass)
    override fun onReceive(message: Any) {
        when (message) {
            is Runnable -> {
                message.run()
            }
            is Function0<*> -> {
                message.invoke()
            }
            else -> unhandled(message)
        }
    }
}