@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.mikai233.common.coroutine

import akka.actor.*
import akka.event.Logging
import akka.pattern.BackoffOpts
import akka.pattern.BackoffSupervisor
import akka.pattern.Patterns
import com.mikai233.common.actor.asExecutor
import com.mikai233.common.actor.tellNoSender
import com.mikai233.common.tools.threadName
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import scala.concurrent.duration.FiniteDuration
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * @author mikai233
 * @date 2022/2/15
 */

class WorkerActorNullException : Exception() {
    override val message: String
        get() = "Worker actor must not be null, if you want to use worker, override the actor props, default props set to null"
}

sealed class WorkerName {
    object IO : WorkerName()
    object Compute : WorkerName()
    data class Named(val name: String) : WorkerName()
}

interface ActorCoroutineHolder {
    fun akkaCoroutine(): AkkaCoroutine
}

class AkkaCoroutine(
    val name: String,
    private val actorContext: ActorContext,
    io: Props? = null,
    compute: Props? = null,
    named: Map<String, Props> = emptyMap(),
    val maxRootJobs: Int = 100000
) : CoroutineScope {
    private val logger = Logging.getLogger(actorContext.system(), javaClass)
    override val coroutineContext: CoroutineContext
        get() = CoroutineName(name) + actorContext.dispatcher().asCoroutineDispatcher() + SupervisorJob()
    val jobs: LinkedList<Job> = LinkedList()
    val ioActor: ActorRef?
    val computeActor: ActorRef?
    val namedActors: Map<String, ActorRef>
    val defaultDispatcher: CoroutineDispatcher = actorContext.dispatcher().asCoroutineDispatcher()
    val ioDispatcher: CoroutineDispatcher?
    val computeDispatcher: CoroutineDispatcher?

    init {
        ioActor = if (io != null) actorContext.actorOf(withSupervisorProps("io-actor", io), "io-actor") else null
        computeActor = if (compute != null) actorContext.actorOf(
            withSupervisorProps("compute-actor", compute), "compute-actor"
        ) else null
        namedActors = named.map { (name, props) ->
            name to actorContext.actorOf(withSupervisorProps("$name-actor", props), "$name-actor")
        }.associate { it }
        ioDispatcher = ioActor?.asExecutor("io-executor")?.asCoroutineDispatcher()
        computeDispatcher = computeActor?.asExecutor("compute-executor")?.asCoroutineDispatcher()
    }

    fun withSupervisorProps(name: String, props: Props): Props {
        return BackoffSupervisor.props(
            BackoffOpts.onFailure(
                props, name, FiniteDuration(1, TimeUnit.SECONDS), FiniteDuration(5, TimeUnit.SECONDS), 0.5
            )
        )
    }

    suspend fun <T> io(block: suspend () -> T): T {
        return withContext(requireNotNull(ioDispatcher) { "io props must set when use io suspend function" }) {
            block.invoke()
        }
    }

    suspend fun <T> compute(block: suspend () -> T): T {
        return withContext(requireNotNull(computeDispatcher) { "compute props must set when use compute suspend function" }) {
            block.invoke()
        }
    }

    suspend fun <T> default(block: () -> T): T {
        return withContext(coroutineContext) {
            block.invoke()
        }
    }

    suspend fun <T> named(name: String, block: () -> T): T {
        val namedDispatcher = namedActors["name"] ?: throw WorkerActorNullException()
        return withContext(namedDispatcher.asExecutor(name).asCoroutineDispatcher()) {
            block.invoke()
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T, R> ask(actorRef: ActorRef, msg: T, timeOut: Duration): R {
        return Patterns.ask(actorRef, msg, timeOut.toJavaDuration()).await() as R
    }

    fun launch(context: CoroutineContext = EmptyCoroutineContext, block: suspend AkkaCoroutine.() -> Unit) {
        removeCompleteJobs()
        checkMaxRootJobs()
        val job = CoroutineScope(coroutineContext + context).launch { block.invoke(this@AkkaCoroutine) }
        jobs.addLast(job)
    }

    private fun removeCompleteJobs() {
        val iter = jobs.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.isCompleted) {
                iter.remove()
            }
        }
    }

    private fun checkMaxRootJobs() {
        if (maxRootJobs > 0 && jobs.size >= maxRootJobs) {
            logger.warning("too many coroutine root jobs, max root jobs:{}, poll first job and cancel it", maxRootJobs)
            val job = jobs.pollFirst()
            job.cancel(CancellationException("too many coroutine root jobs, max root jobs:$maxRootJobs"))
        }
    }

}

class TestCoroutineActor : UntypedAbstractActor(), ActorCoroutineHolder {
    companion object {
        fun props(): Props = Props.create(TestCoroutineActor::class.java)
    }

    private val logger = Logging.getLogger(context.system, javaClass)
    private lateinit var akkaCoroutine: AkkaCoroutine
    override fun preStart() {
        akkaCoroutine = AkkaCoroutine(
            javaClass.simpleName,
            context,
            WorkerActor.props("io-worker").withDispatcher("akka.actor.io-dispatcher"),
            WorkerActor.props("compute-worker").withDispatcher("akka.actor.compute-dispatcher"),
            emptyMap(),
            10
        )
    }

    override fun onReceive(message: Any) {
        akkaCoroutine().launch {
            val result = io {
                println("before delay:${threadName()}")
                delay(5000)
                println("after delay:${threadName()}")
                message
            }
            println(result)
            println(threadName())
        }
    }

    override fun akkaCoroutine(): AkkaCoroutine = akkaCoroutine

}

fun main() {
    val str = """
akka {
  loggers = ["akka.event.slf4j.Slf4jLogger"]
  loglevel = "INFO"
  stdout-loglevel = "INFO"
  logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
  loggers-dispatcher = "akka.actor.logger-dispatcher"

  actor {

    logger-dispatcher {
      type = Dispatcher
      executor = "thread-pool-executor"
      thread-pool-executor {
        fixed-pool-size = 1
      }
      throughput = 100
    }

    io-dispatcher {
      type = Dispatcher
      executor = "thread-pool-executor"
      thread-pool-executor {
        core-pool-size-min = 1
      }
      throughput = 1
    }

    compute-dispatcher {
      type = Dispatcher
      executor = "fork-join-executor"
    }
    throughput = 100
  }
}
    """.trimIndent()
    val config = ConfigFactory.parseString(str)
    val actorSystem = ActorSystem.create("test", config)
    val testActor = actorSystem.actorOf(TestCoroutineActor.props())
    repeat(20) {
        testActor.tellNoSender(it)
    }
}