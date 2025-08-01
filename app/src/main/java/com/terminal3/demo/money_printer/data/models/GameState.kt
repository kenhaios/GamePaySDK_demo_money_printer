package com.terminal3.demo.money_printer.data.models

data class GameState(
    val cash: Long = 0,
    val coins: Int = 0,
    val automationTools: Map<String, Int> = emptyMap(),
    val totalEarned: Long = 0,
    val lastSaveTime: Long = System.currentTimeMillis()
)