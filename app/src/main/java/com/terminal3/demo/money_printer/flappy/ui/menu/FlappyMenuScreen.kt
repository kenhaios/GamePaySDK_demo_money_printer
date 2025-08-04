package com.terminal3.demo.money_printer.flappy.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FlappyMenuScreen(
    coins: Int,
    onPlay: () -> Unit,
    onCoinShop: () -> Unit,
    onThemeShop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Flappy Flight", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Coins: $coins")
        Button(onClick = onPlay) { Text("Play") }
        Button(onClick = onCoinShop) { Text("Coin Shop") }
        Button(onClick = onThemeShop) { Text("Theme Shop") }
    }
}
