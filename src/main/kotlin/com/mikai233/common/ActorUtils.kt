package com.mikai233.common

import akka.actor.AbstractActor
import akka.actor.Props
import akka.event.Logging
import akka.event.LoggingAdapter

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */

inline fun <reified T> actorProps(vararg args: Any): Props = Props.create(T::class.java, *args)

fun AbstractActor.actorLogger(): LoggingAdapter {
    return Logging.getLogger(context.system, javaClass)
}

inline fun <reified T> AbstractActor.actorLogger(logSource: Any): LoggingAdapter {
    return Logging.getLogger(context.system, logSource)
}

fun AbstractActor.logPreStart(logger: LoggingAdapter) {
    logger.info("{} preStart", self)
}

fun AbstractActor.logPostStop(logger: LoggingAdapter) {
    logger.info("{} preStart", self)
}
