package com.terminal3.demo.money_printer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.terminal3.demo.money_printer.ui.main.MainGameScreen
import com.terminal3.demo.money_printer.ui.shop.CoinShopScreen

enum class Screen {
    MAIN_GAME,
    COIN_SHOP
}

@Composable
fun MoneyFactoryApp() {
    var currentScreen by remember { mutableStateOf(Screen.MAIN_GAME) }
    
    when (currentScreen) {
        Screen.MAIN_GAME -> {
            MainGameScreen(
                onNavigateToShop = { currentScreen = Screen.COIN_SHOP }
            )
        }
        Screen.COIN_SHOP -> {
            CoinShopScreen(
                onBack = { currentScreen = Screen.MAIN_GAME }
            )
        }
    }
}