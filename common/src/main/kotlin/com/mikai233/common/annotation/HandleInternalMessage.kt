package com.mikai233.common.annotation

import com.mikai233.common.actor.ActorState
import com.mikai233.common.message.InternalMessage
import kotlin.reflect.KClass

/**
 * 此注解用于标注一个Function处理对应的[InternalMessage]消息
 * @param msg 需要处理的内部消息
 * @param actorStates 这些消息允许在哪些ActorState进行处理
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class HandleInternalMessage(
    val msg: KClass<out InternalMessage>,
    val actorStates: Array<ActorState> = [ActorState.Uninitialized, ActorState.Initializing, ActorState.Up, ActorState.Stopping, ActorState.Stopped]
)
