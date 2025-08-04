package com.terminal3.demo.money_printer.flappy

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.terminal3.demo.money_printer.flappy.ui.menu.FlappyMenuScreen
import com.terminal3.demo.money_printer.flappy.ui.play.FlappyPlayScreen
import com.terminal3.demo.money_printer.flappy.ui.shop.FlappyCoinShopScreen
import com.terminal3.demo.money_printer.flappy.ui.shop.FlappyThemeShopScreen

enum class FlappyScreen {
    MENU,
    COIN_SHOP,
    THEME_SHOP,
    PLAY
}

@Composable
fun FlappyGameApp(onBack: () -> Unit) {
    var currentScreen by remember { mutableStateOf(FlappyScreen.MENU) }
    val viewModel: FlappyViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (currentScreen) {
        FlappyScreen.MENU -> {
            BackHandler { onBack() }
            FlappyMenuScreen(
                coins = uiState.coins,
                onPlay = { currentScreen = FlappyScreen.PLAY },
                onCoinShop = { currentScreen = FlappyScreen.COIN_SHOP },
                onThemeShop = { currentScreen = FlappyScreen.THEME_SHOP }
            )
        }
        FlappyScreen.COIN_SHOP -> {
            BackHandler { currentScreen = FlappyScreen.MENU }
            FlappyCoinShopScreen(
                onBack = { currentScreen = FlappyScreen.MENU },
                onPurchase = { viewModel.addCoins(it) }
            )
        }
        FlappyScreen.THEME_SHOP -> {
            BackHandler { currentScreen = FlappyScreen.MENU }
            FlappyThemeShopScreen(
                uiState = uiState,
                onBack = { currentScreen = FlappyScreen.MENU },
                onPurchase = { viewModel.purchase(it) },
                onSelect = { viewModel.select(it) }
            )
        }
        FlappyScreen.PLAY -> {
            BackHandler { currentScreen = FlappyScreen.MENU }
            FlappyPlayScreen(
                uiState = uiState,
                onBack = { currentScreen = FlappyScreen.MENU }
            )
        }
    }
}
