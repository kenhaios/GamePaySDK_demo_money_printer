package com.terminal3.demo.money_printer.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.terminal3.demo.money_printer.flappy.FlappyGameApp
import com.terminal3.demo.money_printer.ui.gamelist.GameListScreen

enum class LauncherScreen {
    GAME_LIST,
    MONEY_FACTORY,
    FLAPPY
}

@Composable
fun GameLauncherApp() {
    var currentScreen by remember { mutableStateOf(LauncherScreen.GAME_LIST) }

    when (currentScreen) {
        LauncherScreen.GAME_LIST -> {
            GameListScreen(
                onLaunchMoneyFactory = { currentScreen = LauncherScreen.MONEY_FACTORY },
                onLaunchFlappy = { currentScreen = LauncherScreen.FLAPPY }
            )
        }
        LauncherScreen.MONEY_FACTORY -> {
            BackHandler { currentScreen = LauncherScreen.GAME_LIST }
            MoneyFactoryApp()
        }
        LauncherScreen.FLAPPY -> {
            FlappyGameApp(onBack = { currentScreen = LauncherScreen.GAME_LIST })
        }
    }
}
