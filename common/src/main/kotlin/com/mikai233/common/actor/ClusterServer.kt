package com.mikai233.common.actor

import akka.actor.ActorSystem
import com.mikai233.common.annotation.AllOpen

@AllOpen
abstract class ClusterServer(val clusterRole: ClusterRole) {
    lateinit var actorSystem: ActorSystem
        private set

    fun preCreateActorSystem() {

    }

    fun createActorSystem() {

    }

    fun postCreateActorSystem() {

    }

    fun bootstrap() {

    }
}