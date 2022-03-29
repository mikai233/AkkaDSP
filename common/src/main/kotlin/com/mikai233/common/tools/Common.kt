package com.mikai233.common.tools

import org.apache.logging.log4j.kotlin.KotlinLogger
import org.reflections.Reflections
import java.lang.reflect.Constructor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun threadName(): String = Thread.currentThread().name

inline fun <reified T> findAllSubClasses(packageName: String): Set<Class<out T>> {
    val reflections = Reflections(packageName)
    return reflections.getSubTypesOf(T::class.java)
}

inline fun <reified T> findNoArgConstructor(clazz: Class<*>): Constructor<*> {
    return clazz.constructors.find { it.parameterCount == 0 } ?: throw RuntimeException("No NoArg Constructor")
}

@OptIn(ExperimentalContracts::class)
inline fun <T : Any> requireNull(value: T?, lazyMessage: () -> Any) {
    contract {
        returns() implies (value == null)
    }
    if (value != null) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    }
}

@OptIn(ExperimentalContracts::class)
fun <T : Any> requireNull(value: T?) {
    contract {
        returns() implies (value == null)
    }
    return requireNull(value) { "Required value was not null." }
}

fun KotlinLogger.unhandled(msg: Any) {
    warn { "message:${msg.javaClass.simpleName} was unhanded" }
}