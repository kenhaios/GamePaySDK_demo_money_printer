package com.terminal3.demo.money_printer.data.models

data class CoinExchangeOption(
    val coinsRequired: Int,
    val cashReceived: Long,
    val bonusPercentage: Int,
    val description: String
) {
    val isPopular: Boolean = bonusPercentage >= 20
}

object CoinExchangeOptions {
    val ALL_OPTIONS = listOf(
        CoinExchangeOption(
            coinsRequired = 1,
            cashReceived = 100L,
            bonusPercentage = 0,
            description = "Basic exchange"
        ),
        CoinExchangeOption(
            coinsRequired = 10,
            cashReceived = 1100L,
            bonusPercentage = 10,
            description = "10% bonus"
        ),
        CoinExchangeOption(
            coinsRequired = 50,
            cashReceived = 6000L,
            bonusPercentage = 20,
            description = "20% bonus"
        ),
        CoinExchangeOption(
            coinsRequired = 100,
            cashReceived = 13000L,
            bonusPercentage = 30,
            description = "30% bonus"
        )
    )
}