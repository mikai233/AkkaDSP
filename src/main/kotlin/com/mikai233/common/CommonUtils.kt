package com.mikai233.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/27
 */
inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

fun logger(name: String): Logger {
    return LoggerFactory.getLogger(name)
}