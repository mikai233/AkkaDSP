package com.mikai233.common.config

interface Item {
    val id: Int
    var amount: Int

    fun key(): Int = id
    fun zero(): Item
    fun copy(amount: Int = this.amount): Item
}