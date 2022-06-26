package com.mikai233.dsp.item

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2022/6/26
 */
data class Item(val type: ItemType, val num: Long) {

    operator fun plus(other: Item): Item {
        typeCheck(other)
        return copy(num = num + other.num)
    }

    operator fun plus(n: Long): Item {
        return copy(num = num + n)
    }

    operator fun plus(n: Int): Item {
        return copy(num = num + n)
    }

    operator fun minus(other: Item): Item {
        typeCheck(other)
        return copy(num = num - other.num)
    }

    operator fun minus(n: Long): Item {
        return copy(num = num - n)
    }

    operator fun minus(n: Int): Item {
        return copy(num = num - n)
    }

    operator fun inc(): Item {
        return copy(num = num + 1)
    }

    operator fun dec(): Item {
        return copy(num = num - 1)
    }

    operator fun compareTo(other: Item): Int {
        typeCheck(other)
        return num.compareTo(other.num)
    }

    private fun typeCheck(other: Item) {
        require(other.type == type) { "the plus item:$type not same as $type" }
    }

    fun enough(item: Item): Boolean {
        typeCheck(item)
        return num >= item.num
    }

    fun enough(num: Long): Boolean {
        return this.num >= num
    }

    operator fun times(n: Int): Item {
        return copy(num = num * n)
    }
}