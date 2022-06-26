package com.mikai233.common

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.mikai233.actor.PlanetActor

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */
fun ActorSystem.genPlanets(num: Int): Set<ActorRef> {
    val actors = mutableSetOf<ActorRef>()
    repeat(num) {
        val actorRef = actorOf(actorProps<PlanetActor>(), "Planet-$it")
        actors.add(actorRef)
    }
    return actors
}