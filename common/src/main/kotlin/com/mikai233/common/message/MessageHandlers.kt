@file:Suppress("DuplicatedCode", "unused")

package com.mikai233.common.message

import akka.actor.AbstractActor
import com.google.protobuf.GeneratedMessageV3
import com.mikai233.common.actor.ActorState
import com.mikai233.common.annotation.HandleInternalMessage
import com.mikai233.common.annotation.HandleProtoMessage
import com.mikai233.common.tools.findAllSubClasses
import com.mikai233.common.tools.requireNull
import com.mikai233.common.tools.unhandled
import org.apache.logging.log4j.kotlin.logger
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

/**
 * 扫描指定包下面的所有[MessageHandler]，利用反射创建实例存入Map，根据Actor收到的消息类型找到对应的[MessageHandler]进行处理，一个节点创建
 * 一个[MessageHandlers]处理消息即可，一个节点存在多个分片或者多个Actor需要处理消息时可以共用此[MessageHandlers]
 */
class MessageHandlers(vararg packages: String) {
    companion object {
        val logger = MessageHandlers.logger()
    }

    private val packages = packages.toSet()
    private val handlers: MutableMap<KClass<out MessageHandler>, MessageHandler> = mutableMapOf()
    private val protoMessageHandlers: MutableMap<KClass<out GeneratedMessageV3>, Triple<KClass<out MessageHandler>, KFunction<*>, Set<ActorState>>> =
        mutableMapOf()
    private val internalMessageHandlers: MutableMap<KClass<out InternalMessage>, Triple<KClass<out MessageHandler>, KFunction<*>, Set<ActorState>>> =
        mutableMapOf()

    init {
        initAllPackageHandlers()
        initProtoMessageHandlers()
        initInternalMessageHandlers()
    }

    private fun initAllPackageHandlers() {
        packages.forEach { eachPackage ->
            findAllSubClasses<MessageHandler>(eachPackage).forEach { clazz ->
                val instance = clazz.getDeclaredConstructor().newInstance()
                handlers[clazz.kotlin] = instance
            }
        }
    }

    private fun initInternalMessageHandlers() {
        handlers.keys.forEach { handlerKClass ->
            handlerKClass.declaredFunctions.forEach {
                it.findAnnotation<HandleProtoMessage>()?.let { handleProtoMessage ->
                    requireNull(protoMessageHandlers[handleProtoMessage.msg]) { "Duplicate annotations to process ${handleProtoMessage.msg.simpleName} message" }
                    protoMessageHandlers[handleProtoMessage.msg] =
                        Triple(handlerKClass, it, handleProtoMessage.actorStates.toSet())
                }
            }
        }
    }

    private fun initProtoMessageHandlers() {
        handlers.keys.forEach { handlerKClass ->
            handlerKClass.declaredFunctions.forEach {
                it.findAnnotation<HandleInternalMessage>()?.let { handleInternalMessage ->
                    requireNull(internalMessageHandlers[handleInternalMessage.msg]) { "Duplicate annotations to process ${handleInternalMessage.msg.simpleName} message" }
                    internalMessageHandlers[handleInternalMessage.msg] =
                        Triple(handlerKClass, it, handleInternalMessage.actorStates.toSet())
                }
            }
        }
    }

    fun handleProtoMessage(
        actor: AbstractActor, currentState: ActorState, msg: GeneratedMessageV3, params: Map<String, Any> = emptyMap()
    ) {
        val (handlerKClass, function, states) = protoMessageHandlers[msg::class] ?: run {
            logger.unhandled(msg)
            return
        }
        val handlerInstance =
            requireNotNull(handlers[handlerKClass]) { "Handler instance ${handlerKClass.simpleName} not found" }
        invoke(function, handlerInstance, currentState, states, actor, msg, params)
    }

    fun handleInternalMessage(
        actor: AbstractActor, currentState: ActorState, msg: InternalMessage, params: Map<String, Any> = emptyMap()
    ) {
        val (handlerKClass, function, allowedStates) = internalMessageHandlers[msg::class] ?: run {
            return
        }
        val handlerInstance =
            requireNotNull(handlers[handlerKClass]) { "Handler instance ${handlerKClass.simpleName} not found" }
        invoke(function, handlerInstance, currentState, allowedStates, actor, msg, params)
    }

    /**
     * 热更替换对应的Handler
     */
    fun replaceHandler(handler: KClass<out MessageHandler>, instance: MessageHandler): Boolean {
        return try {
            requireNotNull(handlers[handler]) { "Handler:${handler.simpleName} not found in handlers" }
            logger.info { "${javaClass.simpleName} packages:${packages} start to replace handler:${handler.simpleName}" }
            handlers[handler] = instance
            logger.info { "${javaClass.simpleName} packages:${packages} replace handler:${handler.simpleName} done" }
            true
        } catch (e: Exception) {
            logger.error(e)
            false
        }
    }

    private fun invoke(
        function: KFunction<*>,
        handler: MessageHandler,
        currentState: ActorState,
        allowedStates: Set<ActorState>,
        actor: AbstractActor,
        msg: Any,
        params: Map<String, Any>
    ) {
        if (currentState in allowedStates) {
            when (val parametersCount = function.parameters.count()) {
                2 -> function.call(handler, actor)
                3 -> function.call(handler, actor, msg)
                4 -> function.call(handler, actor, msg, params)
                else -> throw Exception("too many parameters $parametersCount")
            }
        } else {
            logger.warn { "Message:${msg::class.simpleName} is not allowed to process in current actor state:${currentState}, all allowed states:${allowedStates}" }
        }
    }
}