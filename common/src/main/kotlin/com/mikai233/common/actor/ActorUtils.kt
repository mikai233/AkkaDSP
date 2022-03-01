@file:Suppress("unused")

package com.mikai233.common.actor

import akka.actor.*
import akka.event.Logging
import com.mikai233.common.coroutine.NamedRunnable
import com.mikai233.common.message.Tick
import java.time.Duration
import java.util.concurrent.Executor

class SupervisorStrategyActor(private val props: Props, private val supervisorStrategy: SupervisorStrategy) :
    AbstractActor() {
    private val logger = Logging.getLogger(context.system, javaClass)

    companion object {
        fun props(props: Props, supervisorStrategy: SupervisorStrategy): Props =
            Props.create(SupervisorStrategyActor::class.java, props, supervisorStrategy)
    }

    private lateinit var child: ActorRef
    override fun preStart() {
        child = context.actorOf(props)
        context.watch(child)
    }

    override fun createReceive(): Receive {
        return receiveBuilder().match(Terminated::class.java) { terminated ->
            if (sender == child) {
                context.stop(self)
                logger.info("{} received child's terminated msg, stop self", javaClass.simpleName)
            } else {
                child.forward(terminated, context)
            }
        }.matchAny { msg ->
            child.forward(msg, context)
        }.build()
    }

    override fun supervisorStrategy(): SupervisorStrategy {
        return supervisorStrategy
    }
}

fun ActorRef.tellNoSender(msg: Any) {
    tell(msg, ActorRef.noSender())
}

fun Actor.execute(runnable: Runnable) {
    self().tellNoSender(runnable)
}

fun Actor.execute(block: () -> Unit) {
    self().tellNoSender(block)
}

fun Actor.execute(taskName: String, block: () -> Unit) {
    self().tellNoSender(NamedRunnable(taskName, block))
}

fun ActorRef.asExecutor(executorName: String? = null): Executor {
    return if (executorName == null) {
        Executor(::tellNoSender)
    } else {
        Executor { command: Runnable ->
            tellNoSender(NamedRunnable(executorName, command::run))
        }
    }
}

fun AbstractActor.scheduleTick(
    initialDelay: Duration = Duration.ofSeconds(1), interval: Duration = Duration.ofSeconds(1), message: Any = Tick
): Cancellable {
    return context.system.scheduler.scheduleAtFixedRate(initialDelay, interval, {
        self.tellNoSender(message)
    }, context.dispatcher)
}