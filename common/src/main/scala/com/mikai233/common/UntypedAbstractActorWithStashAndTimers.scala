package com.mikai233.common

import akka.actor.{Stash, Timers, UntypedAbstractActor}

abstract class UntypedAbstractActorWithStashAndTimers extends UntypedAbstractActor with Stash with Timers {

}
