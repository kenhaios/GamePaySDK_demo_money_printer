package com.terminal3.demo.money_printer.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.terminal3.demo.money_printer.data.models.AutomationTool
import com.terminal3.demo.money_printer.utils.NumberFormatter

@Composable
fun MainGameScreen(
    onNavigateToShop: () -> Unit = {},
    viewModel: MainGameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val moneyPerSecond by viewModel.moneyPerSecond.collectAsState()
    val clickAnimation by viewModel.clickAnimation.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E2E2E))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Stats Panel
            StatsPanel(
                cash = uiState.cash,
                coins = uiState.coins,
                moneyPerSecond = moneyPerSecond,
                onShopClick = onNavigateToShop
            )
            
            // Factory and Click Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                FactoryClickButton(
                    isAnimating = clickAnimation,
                    onClick = { viewModel.onMoneyClick() }
                )
            }
            
            // Automation Tools List
            AutomationToolsList(
                tools = uiState.automationTools,
                userCash = uiState.cash,
                onPurchase = { toolId -> viewModel.purchaseAutomationTool(toolId) }
            )
        }
        
        // Offline Earnings Dialog
        if (uiState.showOfflineEarnings) {
            OfflineEarningsDialog(
                earnings = uiState.offlineEarningsAmount,
                onDismiss = { viewModel.dismissOfflineEarnings() }
            )
        }
    }
}

@Composable
fun StatsPanel(
    cash: Long,
    coins: Int,
    moneyPerSecond: Double,
    onShopClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF424242))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cash",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = NumberFormatter.formatMoney(cash),
                        color = Color(0xFF4CAF50),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Coins",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = coins.toString(),
                            color = Color(0xFFFFD700),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = onShopClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "+ Buy",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Income: ${NumberFormatter.formatRate(moneyPerSecond)}",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FactoryClickButton(
    isAnimating: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200)
    )
    
    Box(
        modifier = Modifier
            .size(150.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0xFF4CAF50))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💰",
                fontSize = 48.sp
            )
            Text(
                text = "PRINT MONEY",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AutomationToolsList(
    tools: List<AutomationTool>,
    userCash: Long,
    onPurchase: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF424242))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Automation Tools",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(tools) { tool ->
                    AutomationToolItem(
                        tool = tool,
                        canAfford = userCash >= tool.getCurrentCost(),
                        onPurchase = { onPurchase(tool.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AutomationToolItem(
    tool: AutomationTool,
    canAfford: Boolean,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) Color(0xFF616161) else Color(0xFF424242)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${tool.name} (${tool.owned})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = tool.description,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                if (tool.owned > 0) {
                    Text(
                        text = "Producing: ${NumberFormatter.formatRate(tool.getCurrentProduction())}",
                        color = Color(0xFF4CAF50),
                        fontSize = 11.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = onPurchase,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    disabledContainerColor = Color(0xFF616161)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = NumberFormatter.formatMoney(tool.getCurrentCost()),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun OfflineEarningsDialog(
    earnings: Long,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF424242))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "You earned while away:",
                    color = Color.White,
                    fontSize = 14.sp
                )
                
                Text(
                    text = NumberFormatter.formatMoney(earnings),
                    color = Color(0xFF4CAF50),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Collect", color = Color.White)
                }
            }
        }
    }
}