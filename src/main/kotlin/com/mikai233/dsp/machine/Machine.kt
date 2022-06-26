package com.mikai233.dsp.machine

import com.mikai233.common.TickTimer
import com.mikai233.common.Ticker
import com.mikai233.common.logger
import com.mikai233.dsp.Recipe

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */
abstract class Machine : Ticker {
    companion object {
        val logger = Machine.logger()
    }

    lateinit var recipe: Recipe
        protected set
    var state: MachineState = MachineState.STOP
        protected set
    var timer: TickTimer = TickTimer(base, recipe.speed)
        protected set

    abstract fun process()

    override fun tick() {
        if (state == MachineState.WORK && this::recipe.isInitialized) {
            timer.invokeOnTimeUp {
                process()
            }
        }
    }

    fun configRecipe(newRecipe: Recipe) {
        recipe = newRecipe
        timer = TickTimer(base, newRecipe.speed)
    }
}