package com.terminal3.demo.money_printer.flappy.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FlappyCoinShopScreen(
    onBack: () -> Unit,
    onPurchase: (Int) -> Unit
) {
    val packages = listOf(
        100 to "\$0.99",
        500 to "\$2.99",
        1000 to "\$4.99",
        2500 to "\$9.99"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        packages.forEach { (amount, price) ->
            Button(onClick = { onPurchase(amount) }) {
                Text(text = "$amount coins - $price")
            }
        }
        Button(onClick = onBack) { Text("Back") }
    }
}
