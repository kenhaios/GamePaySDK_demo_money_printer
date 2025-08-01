package com.terminal3.demo.money_printer.ui.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.terminal3.demo.money_printer.data.models.PremiumItem
import com.terminal3.demo.money_printer.data.models.PremiumItemType
import com.terminal3.demo.money_printer.ui.common.ConfirmationDialog

@Composable
fun PremiumToolsScreen(
    viewModel: PremiumToolsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmation by remember { mutableStateOf<PremiumItem?>(null) }
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Active boosts section
        if (uiState.activeBoosts.isNotEmpty()) {
            Text(
                text = "Active Boosts",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A5C2F))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    uiState.activeBoosts.forEach { (name, timeLeft) ->
                        ActiveBoostItem(name = name, timeLeftMs = timeLeft)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Premium items
        Text(
            text = "Premium Tools",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.premiumItems) { item ->
                PremiumItemCard(
                    item = item,
                    isOwned = uiState.ownedItems.contains(item.id),
                    canAfford = uiState.currentCoins >= item.coinCost,
                    onPurchase = { 
                        if (item.coinCost >= 200) {
                            showConfirmation = item
                        } else {
                            viewModel.purchaseItem(item.id, item.coinCost)
                        }
                    }
                )
            }
        }
    }
    
    // Confirmation dialog
    showConfirmation?.let { item ->
        ConfirmationDialog(
            title = "Expensive Purchase",
            message = "Are you sure you want to purchase ${item.name} for ${item.coinCost} coins?\n\n${item.description}",
            onConfirm = {
                viewModel.purchaseItem(item.id, item.coinCost)
                showConfirmation = null
            },
            onCancel = {
                showConfirmation = null
            }
        )
    }
}

@Composable
fun ActiveBoostItem(name: String, timeLeftMs: Long) {
    val minutes = (timeLeftMs / 60000).toInt()
    val seconds = ((timeLeftMs % 60000) / 1000).toInt()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "⚡ $name",
            color = Color(0xFFFFD700),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${minutes}m ${seconds}s",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PremiumItemCard(
    item: PremiumItem,
    isOwned: Boolean,
    canAfford: Boolean,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isOwned -> Color(0xFF2E7D32)
                canAfford -> Color(0xFF424242)
                else -> Color(0xFF2E2E2E)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isOwned) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✓ OWNED",
                                color = Color(0xFF4CAF50),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = item.description,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Effect details
                    val effectText = when (item.type) {
                        PremiumItemType.PERMANENT_AUTOMATION_MULTIPLIER -> 
                            "Permanent ${item.effectValue}x automation boost"
                        PremiumItemType.PERMANENT_CLICK_MULTIPLIER -> 
                            "Permanent ${item.effectValue.toInt()}x click boost"
                        PremiumItemType.TEMPORARY_PRODUCTION_BOOST -> 
                            "${item.effectValue}x production for ${item.durationMinutes}min"
                        PremiumItemType.TEMPORARY_AUTO_COLLECT -> 
                            "Auto-collect ${(item.effectValue * 100).toInt()}% per second for ${item.durationMinutes}min"
                        PremiumItemType.TEMPORARY_MEGA_BOOST -> 
                            "${item.effectValue.toInt()}x all production for ${item.durationMinutes}min"
                    }
                    
                    Text(
                        text = effectText,
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onPurchase,
                    enabled = !isOwned && canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        disabledContainerColor = Color(0xFF616161)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isOwned) "OWNED" else "${item.coinCost} 💰",
                        color = if (isOwned) Color.White else Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}