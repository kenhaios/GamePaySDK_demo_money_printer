package com.terminal3.demo.money_printer.flappy.ui.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.terminal3.demo.money_printer.flappy.FlappyUiState
import com.terminal3.demo.money_printer.flappy.ItemType
import com.terminal3.demo.money_printer.flappy.ShopItem

@Composable
fun FlappyThemeShopScreen(
    uiState: FlappyUiState,
    onBack: () -> Unit,
    onPurchase: (ShopItem) -> Unit,
    onSelect: (ShopItem) -> Unit
) {
    val items = listOf(
        ShopItem("red", "Red Bird", 200, ItemType.BIRD),
        ShopItem("night", "Night Theme", 150, ItemType.THEME)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            val owned = when (item.type) {
                ItemType.BIRD -> item.id in uiState.ownedBirds
                ItemType.THEME -> item.id in uiState.ownedThemes
            }
            val selected = when (item.type) {
                ItemType.BIRD -> uiState.selectedBird == item.id
                ItemType.THEME -> uiState.selectedTheme == item.id
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (owned) onSelect(item) else onPurchase(item)
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF424242))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.name, color = Color.White)
                    Text(
                        text = when {
                            owned && selected -> "Selected"
                            owned -> "Select"
                            else -> "${item.cost} coins"
                        },
                        color = if (owned) Color(0xFF4CAF50) else Color.White
                    )
                }
            }
        }
        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Back") }
    }
}
