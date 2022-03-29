package com.mikai233.common.message

import akka.actor.NotInfluenceReceiveTimeout

interface InternalMessage

object Tick : InternalMessage, NotInfluenceReceiveTimeout

