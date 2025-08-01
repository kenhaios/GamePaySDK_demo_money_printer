package com.terminal3.demo.money_printer.data.models

data class AutomationTool(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Long,
    val baseProduction: Double,
    val costMultiplier: Double = 1.15,
    var owned: Int = 0
) {
    fun getCurrentCost(): Long = (baseCost * Math.pow(costMultiplier, owned.toDouble())).toLong()
    fun getCurrentProduction(): Double = baseProduction * owned
}

object AutomationTools {
    val ALL_TOOLS = listOf(
        AutomationTool("basic_printer", "Basic Printer", "Simple money printer", 10, 0.1),
        AutomationTool("enhanced_printer", "Enhanced Printer", "Faster money printing", 100, 1.0),
        AutomationTool("money_press", "Money Press", "Industrial money pressing", 1000, 8.0),
        AutomationTool("industrial_printer", "Industrial Printer", "High-volume printing", 12000, 47.0),
        AutomationTool("money_factory", "Money Factory", "Automated money production", 130000, 260.0),
        AutomationTool("mega_factory", "Mega Factory", "Massive money operation", 1400000, 1400.0),
        AutomationTool("corporate_empire", "Corporate Empire", "Global money empire", 20000000, 7800.0)
    )
}