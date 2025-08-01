package com.terminal3.demo.money_printer.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.terminal3.demo.money_printer.ui.exchange.CoinExchangeScreen
import com.terminal3.demo.money_printer.ui.main.EnhancedMainGameScreen
import com.terminal3.demo.money_printer.ui.shop.CoinShopScreen

enum class Screen {
    MAIN_GAME,
    COIN_SHOP,
    COIN_EXCHANGE
}

@Composable
fun MoneyFactoryApp() {
    var currentScreen by remember { mutableStateOf(Screen.MAIN_GAME) }
    
    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith 
            slideOutHorizontally(targetOffsetX = { -it })
        },
        label = "screen_transition"
    ) { targetScreen ->
        when (targetScreen) {
            Screen.MAIN_GAME -> {
                EnhancedMainGameScreen(
                    onNavigateToShop = { currentScreen = Screen.COIN_SHOP },
                    onNavigateToExchange = { currentScreen = Screen.COIN_EXCHANGE }
                )
            }
            Screen.COIN_SHOP -> {
                CoinShopScreen(
                    onBack = { currentScreen = Screen.MAIN_GAME }
                )
            }
            Screen.COIN_EXCHANGE -> {
                CoinExchangeScreen(
                    onBack = { currentScreen = Screen.MAIN_GAME }
                )
            }
        }
    }
}