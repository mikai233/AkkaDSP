package com.mikai233.common.machine

import com.mikai233.common.config.Item
import com.mikai233.common.tools.TickTimer
import kotlin.math.min
import kotlin.time.Duration

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/1/22
 */


data class BaseItem(override val id: Int, override var amount: Int) : Item {
    override fun zero(): Item {
        return BaseItem(id, 0)
    }

    override fun copy(amount: Int): Item {
        return copy(id = id, amount = amount)
    }
}

/**
 * @param powerUsage 负值->消耗 正值->产生
 */
open class Config(
    val input: Map<Int, Item>,
    val machineInputCap: Map<Int, Int>,
    val inputSpeed: Duration,
    val output: Map<Int, Item>,
    val machineOutputCap: Map<Int, Int>,
    val outputSpeed: Duration,
    val transformSpeed: Duration,
    val powerUsage: Double = 0.0
) {
//    override fun toString(): String {
//        return "com.mikai233.common.machine.Config(input=$input, inputSpeed=$inputSpeed, output=$output, outputSpeed=$outputSpeed, powerUsage=$powerUsage)"
//    }
}

data class Coordinate(val x: Double, val y: Double, val z: Double) {
    companion object {
        fun zero(): Coordinate {
            return Coordinate(0.0, 0.0, 0.0)
        }
    }
}

interface MachineTick {
    fun tick(power: Double)
}

enum class MachineState {
    Working, NotWorking, LackPower
}

enum class MachineType {
    Miner, Dynamo,
}

open class Machine(
    val id: Long, val type: MachineType, var config: Config,//Machine的生产配方
    private var tickBase: Duration,//外部调用本Machine的tick时的间隔
    val inputCount: Int,//输入端口数量
    val outputCount: Int,//输出端口数量
    val powerUsage: Double = 0.0,//耗电量 单位W
    var coordinate: Coordinate = Coordinate.zero()//空间坐标
) : MachineTick {
    private val inputCapacity = mutableMapOf<Int, Int>()
    private val outputCapacity = mutableMapOf<Int, Int>()
    private val inputCache = mutableMapOf<Int, Item>()
    private val outputCache = mutableMapOf<Int, Item>()
    var machineState = MachineState.Working
    var lackPowerThreshold = 0.4
    private val inputMachine = mutableMapOf<Item, MutableList<Machine>>()//向本Machine输入item的Machine,key:item
    private val outputMachine = mutableMapOf<Item, MutableList<Machine>>()//本Machine向该Machine输出item,key:item
    private val transformTimer = TickTimer(tickBase, config.transformSpeed)
    private val inputTimer = TickTimer(tickBase, config.inputSpeed)
    private val outputTimer = TickTimer(tickBase, config.outputSpeed)
//    private val transformTimer = Timer(tickBase, config.transformSpeed) {
//        transformItem()
//    }
//    private val inputTimer = Timer(tickBase, config.inputSpeed) {
//        inputItem()
//    }
//    private val outputTimer = Timer(tickBase, config.outputSpeed) {
//        outputItem()
//    }
//    private val logTimer = Timer(tickBase, 1.seconds) {
//        println("inputCache:$inputCache, outputCache:$outputCache")
//    }

    init {
        inputCapacity.putAll(config.machineInputCap)
        outputCapacity.putAll(config.machineOutputCap)
        require(capCheck()) { "Machine的输入输出容量要大于配方的容量" }
    }

    private fun capCheck(): Boolean {
        val i = config.input.all { (itemId, item) ->
            (inputCapacity[itemId] ?: 0) >= item.amount
        }
        val o = config.output.all { (itemId, item) ->
            (outputCapacity[itemId] ?: 0) >= item.amount
        }
        return i && o
    }

    fun linkInput(item: Item, machine: Machine): Boolean {
        return if (inputMachine.values.size < inputCount) {
            inputMachine.getOrPut(item) { mutableListOf() }.add(machine)
            true
        } else {
            false
        }
    }

    @Suppress("DuplicatedCode")
    fun unlinkInput(item: Item, machineId: Long): Boolean {
        return inputMachine[item]?.let { machines ->
            if (machines.size == 1 && machines.first().id == machineId) {
                inputMachine.remove(item) != null
            } else {
                machines.removeIf { it.id == machineId }
            }
        } ?: false
    }

    @Suppress("DuplicatedCode")
    fun unlinkAllInput(machineId: Long) {
        inputMachine.values.forEach { machines ->
            machines.removeAll { it.id == machineId }
        }
        val iter = inputMachine.iterator()
        while (iter.hasNext()) {
            val machine = iter.next()
            if (machine.value.isEmpty()) {
                iter.remove()
            }
        }
    }

    fun linkOutput(item: Item, machine: Machine): Boolean {
        return if (outputMachine.values.size < outputCount) {
            outputMachine.getOrPut(item) { mutableListOf() }.add(machine)
            true
        } else {
            false
        }
    }

    @Suppress("DuplicatedCode")
    fun unlinkOutput(item: Item, machineId: Long): Boolean {
        return inputMachine[item]?.let { machines ->
            if (machines.size == 1 && machines.first().id == machineId) {
                inputMachine.remove(item) != null
            } else {
                machines.removeIf { it.id == machineId }
            }
        } ?: false
    }

    @Suppress("DuplicatedCode")
    fun unlinkAllOutput(machineId: Long) {
        outputMachine.values.forEach { machines ->
            machines.removeAll { it.id == machineId }
        }
        val iter = outputMachine.iterator()
        while (iter.hasNext()) {
            val machine = iter.next()
            if (machine.value.isEmpty()) {
                iter.remove()
            }
        }
    }

    fun changeTickBase(duration: Duration) {
        tickBase = duration
        inputTimer.tickBase = tickBase
        transformTimer.tickBase = tickBase
        outputTimer.tickBase = tickBase
    }

    fun changeConfig(other: Config) {
        config = other
        inputCapacity.clear()
        inputCapacity.putAll(config.machineInputCap)
        outputCapacity.clear()
        outputCapacity.putAll(config.machineOutputCap)
        inputCache.clear()
        outputCache.clear()
    }


    override fun tick(power: Double) {
        setPowerFactor(power)
        when (machineState) {
            MachineState.Working -> {
                inputTimer.invokeOnTimeUp {
                    inputItem()
                }
                transformTimer.invokeOnTimeUp {
                    transformItem()
                }
                outputTimer.invokeOnTimeUp {
                    outputItem()
                }
//                logTimer.tick()
            }
            MachineState.LackPower -> {
                println("machine:${id}, lack power, current power input:$power")
            }
            MachineState.NotWorking -> {
                println("NotWorking")
            }
        }
    }

    protected open fun setPowerFactor(power: Double) {
        if (power >= powerUsage) {
            return
        }
        var factor = powerUsage / power
        if (factor <= lackPowerThreshold) {
            machineState = MachineState.LackPower
            return
        }
        factor = min(factor, 1.0)
        with(inputTimer) {
            interval = config.inputSpeed * factor
        }
        with(transformTimer) {
            interval = config.transformSpeed * factor
        }
        with(outputTimer) {
            interval = config.outputSpeed * factor
        }
    }

    /**
     * 将Item输入到Machine中，返回没有输入完的Item
     * @param item 需要输入Machine中的Item
     */
    open fun input(item: Item): Item {
        if (item.id !in config.input.keys) return item.copy()
        val items = inputCache.getOrPut(item.key()) {
            item.zero()
        }
        val remain = inputRemain(item)
        return if (remain >= item.amount) {
            items.amount += item.amount
            item.zero()
        } else {
            items.amount += remain
            item.copy(item.amount - remain)
        }
    }

    /**
     * @param item 查询此Machine还可以输入多少item
     * @return -1=>非此Machine需要的item >=0=>还可以输入的item数量
     */
    open fun inputRemain(item: Item): Int {
        val inputCap = inputCapacity[item.key()] ?: return -1
        val inputSize = inputCache[item.key()]?.amount ?: return -1
        return inputCap - inputSize
    }

    /**
     * 从Machine中输出指定数量的Item，Machine中Item不足则返回的数量会不足
     * @param item 需要输出的Item
     */
    open fun output(item: Item): Item {
        val outItem = outputCache[item.key()] ?: return item.zero()
        return if (item.amount <= outItem.amount) {
            outItem.amount -= item.amount
            item.copy()
        } else {
            outItem.copy().also {
                outItem.amount = 0
            }
        }
    }

    /**
     * @param item 查询此Machine还可以生产多少此item
     * @return -1=>非此Machine产生的item >=0=>还可以生产的item数量
     */
    open fun outputRemain(item: Item): Int {
        val outCap = outputCapacity[item.key()] ?: return -1
        val cacheSize = outputCache[item.key()]?.amount ?: return -1
        return outCap - cacheSize
    }

    protected open fun inputItem() {
        inputMachine.forEach outerForEach@{ (item, machines) ->
            machines.forEach innerForEach@{ machine ->
                val remain = inputRemain(item)
                if (remain <= 0) {
                    return@outerForEach
                }
                val fetchItem = item.copy(remain)
                val actualFetched = machine.output(fetchItem)
                input(actualFetched)
            }
        }
    }

    protected open fun transformItem() {
        if (checkInputChannel() && checkOutputChannel()) {
            consumeInputChannel()
            produceOutputChannel()
        }
    }

    protected open fun outputItem() {
        outputMachine.forEach outerForEach@{ (item, machines) ->
            machines.forEach innerForEach@{ machine ->
                val remain = machine.inputRemain(item)
                if (remain <= 0) {
                    return@innerForEach
                }
                val outputItem = item.copy(remain)
                val actualOutput = output(outputItem)
                machine.input(actualOutput)
            }
        }
    }

    /**
     * 检查Machine所有已经输入的item是否可以满足配方数量
     */
    protected open fun checkInputChannel(): Boolean {
        return config.input.all { (key, item) ->
            val inputItem = inputCache.getOrPut(key) {
                item.zero()
            }
            inputItem.id == item.id && inputItem.amount >= item.amount
        }
    }

    /**
     * 检查Machine所有的输出通道是否可以放下新生产的item
     */
    protected open fun checkOutputChannel(): Boolean {
        return config.output.all { (key, item) ->
            val outputItem = outputCache.getOrPut(key) {
                item.zero()
            }
            outputItem.id == item.id && outputRemain(item) >= item.amount
        }
    }

    protected open fun consumeInputChannel() {
        config.input.forEach { (key, item) ->
            val inputItem = inputCache[key]!!
            assert(item.id == inputItem.id)
            require(inputItem.amount >= item.amount)
            inputItem.amount -= item.amount
        }
    }

    protected open fun produceOutputChannel() {
        config.output.forEach { (key, item) ->
            val outputItem = outputCache.getOrPut(key) {
                item.zero()
            }
            require(outputRemain(item) >= item.amount)
            outputItem.amount += item.amount
        }
    }
}

fun main() {

}