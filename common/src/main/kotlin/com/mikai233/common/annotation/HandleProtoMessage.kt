package com.mikai233.common.annotation

import com.google.protobuf.GeneratedMessageV3
import com.mikai233.common.actor.ActorState
import kotlin.reflect.KClass

/**
 * 此注解用于标注一个Function处理对应的[GeneratedMessageV3]Proto消息
 * @param msg 需要处理的Proto消息
 * @param actorStates 这些消息允许在哪些ActorState进行处理
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class HandleProtoMessage(
    val msg: KClass<out GeneratedMessageV3>,
    val actorStates: Array<ActorState> = [ActorState.Uninitialized, ActorState.Initializing, ActorState.Up, ActorState.Stopping, ActorState.Stopped]
)
