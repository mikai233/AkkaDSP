package com.mikai233.common

import akka.actor.{AbstractActor, Stash, Timers}

abstract class AbstractActorWithStashAndTimers extends AbstractActor with Stash with Timers {

}
