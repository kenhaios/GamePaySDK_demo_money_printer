package com.terminal3.demo.money_printer.data.models

data class PremiumItem(
    val id: String,
    val name: String,
    val description: String,
    val coinCost: Int,
    val type: PremiumItemType,
    val effectValue: Double,
    val durationMinutes: Int = 0, // 0 for permanent effects
    val isOwned: Boolean = false
)

enum class PremiumItemType {
    PERMANENT_AUTOMATION_MULTIPLIER,
    PERMANENT_CLICK_MULTIPLIER,
    TEMPORARY_PRODUCTION_BOOST,
    TEMPORARY_AUTO_COLLECT,
    TEMPORARY_MEGA_BOOST
}

data class ActiveBoost(
    val itemId: String,
    val type: PremiumItemType,
    val effectValue: Double,
    val endTime: Long
)

object PremiumItems {
    val ALL_ITEMS = listOf(
        PremiumItem(
            id = "super_printer",
            name = "Super Printer",
            description = "2x all automation permanently",
            coinCost = 150,
            type = PremiumItemType.PERMANENT_AUTOMATION_MULTIPLIER,
            effectValue = 2.0
        ),
        PremiumItem(
            id = "golden_touch",
            name = "Golden Touch",
            description = "10x click value permanently",
            coinCost = 300,
            type = PremiumItemType.PERMANENT_CLICK_MULTIPLIER,
            effectValue = 10.0
        ),
        PremiumItem(
            id = "time_accelerator",
            name = "Time Accelerator",
            description = "3x production for 2 hours",
            coinCost = 200,
            type = PremiumItemType.TEMPORARY_PRODUCTION_BOOST,
            effectValue = 3.0,
            durationMinutes = 120
        ),
        PremiumItem(
            id = "money_magnet",
            name = "Money Magnet",
            description = "Auto-collect 5% production per second",
            coinCost = 400,
            type = PremiumItemType.TEMPORARY_AUTO_COLLECT,
            effectValue = 0.05,
            durationMinutes = 60
        ),
        PremiumItem(
            id = "mega_boost",
            name = "Mega Boost",
            description = "10x all production for 30 minutes",
            coinCost = 500,
            type = PremiumItemType.TEMPORARY_MEGA_BOOST,
            effectValue = 10.0,
            durationMinutes = 30
        )
    )
}