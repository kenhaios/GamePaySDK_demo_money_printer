package com.terminal3.demo.money_printer.data.models

data class CoinPackage(
    val id: String,
    val coinAmount: Int,
    val priceUSD: String,
    val bonus: String = "",
    val isPopular: Boolean = false
)

object CoinPackages {
    val ALL_PACKAGES = listOf(
        CoinPackage("small", 100, "$0.99", "Starter Pack"),
        CoinPackage("medium", 500, "$4.99", "+50 Bonus", true),
        CoinPackage("large", 1200, "$9.99", "+200 Bonus"),
        CoinPackage("mega", 2800, "$19.99", "+500 Bonus"),
        CoinPackage("ultimate", 6000, "$39.99", "+1000 Bonus")
    )
}