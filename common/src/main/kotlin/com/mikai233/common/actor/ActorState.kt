package com.mikai233.common.actor

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/2/13
 * Actor状态机
 */

enum class ActorState(val order: Int) {
    Uninitialized(0), Initializing(1), Up(2), Stopping(3), Stopped(4)
}

interface ActorStateI {
    var actorState: ActorState

    fun currentState(): ActorState = actorState
    fun changeState(newState: ActorState): ActorState {
        require(newState.order > currentState().order) { "newActorState:$newState must after currentActorState:${currentState()}" }
        actorState = newState
        onActorStateChange(currentState())
        return currentState()
    }

    fun onActorStateChange(state: ActorState) {
        when (state) {
            ActorState.Uninitialized -> {
                //ignore
            }
            ActorState.Initializing -> onInitializing()
            ActorState.Up -> onUp()
            ActorState.Stopping -> onStopping()
            ActorState.Stopped -> onStopped()
        }
    }

    /**
     * Actor进入Initializing状态回调
     */
    fun onInitializing() {}

    /**
     * Actor进入Up状态回调
     */
    fun onUp() {}

    /**
     * Actor进入Stopping状态回调
     */
    fun onStopping() {}

    /**
     * Actor进入Stopped状态回调
     */
    fun onStopped() {}

    fun goToInitializingState() {
        changeState(ActorState.Initializing)
    }

    fun goToUpState() {
        changeState(ActorState.Up)
    }

    fun goToStoppingState() {
        changeState(ActorState.Stopping)
    }

    fun goToStoppedState() {
        changeState(ActorState.Stopped)
    }
}