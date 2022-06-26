package com.mikai233

import akka.actor.ActorSystem
import com.mikai233.common.genPlanets

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */
object DSP {
    lateinit var actorSystem: ActorSystem
        private set

    private fun createActorSystem() {
        actorSystem = ActorSystem.create("DSP")
    }

    private fun loadUniverse() {
        actorSystem.genPlanets(1000)
    }

    fun start() {
        createActorSystem()
        loadUniverse()
    }
}