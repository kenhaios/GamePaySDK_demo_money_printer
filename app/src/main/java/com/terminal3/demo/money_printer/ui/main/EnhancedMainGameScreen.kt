package com.terminal3.demo.money_printer.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.terminal3.demo.money_printer.ui.premium.PremiumToolsScreen
import com.terminal3.demo.money_printer.utils.NumberFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun EnhancedMainGameScreen(
    onNavigateToShop: () -> Unit = {},
    onNavigateToExchange: () -> Unit = {},
    viewModel: MainGameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val moneyPerSecond by viewModel.moneyPerSecond.collectAsState()
    val clickAnimation by viewModel.clickAnimation.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Automation Tools", "Premium Tools")
    val coroutineScope = rememberCoroutineScope()
    
    // Animated money display
    val animatedCash by animateFloatAsState(
        targetValue = uiState.cash.toFloat(),
        animationSpec = tween(durationMillis = 300),
        label = "cash_animation"
    )
    
    // Money flash effect
    var showMoneyFlash by remember { mutableStateOf(false) }
    val moneyFlashAlpha by animateFloatAsState(
        targetValue = if (showMoneyFlash) 1f else 0f,
        animationSpec = tween(200),
        label = "money_flash"
    )
    
    // Floating money texts
    var floatingTexts by remember { mutableStateOf<List<FloatingText>>(emptyList()) }
    
    LaunchedEffect(uiState.cash) {
        if (uiState.cash > 0) {
            showMoneyFlash = true
            delay(200)
            showMoneyFlash = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E2E2E))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced Stats Panel
            EnhancedStatsPanel(
                cash = animatedCash.toLong(),
                coins = uiState.coins,
                moneyPerSecond = moneyPerSecond,
                moneyFlashAlpha = moneyFlashAlpha,
                onShopClick = onNavigateToShop,
                onExchangeClick = onNavigateToExchange
            )
            
            // Enhanced Factory and Click Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                EnhancedFactoryClickButton(
                    isAnimating = clickAnimation,
                    onClick = { 
                        // TODO: Add haptic feedback when properly configured
                        viewModel.onMoneyClick()
                        
                        // Add floating text
                        floatingTexts = floatingTexts + FloatingText(
                            text = "+1",
                            x = Random.nextFloat() * 100 - 50,
                            y = 0f,
                            id = System.currentTimeMillis()
                        )
                    }
                )
                
                // Floating money texts
                floatingTexts.forEach { floatingText ->
                    FloatingMoneyText(
                        text = floatingText.text,
                        startX = floatingText.x,
                        startY = floatingText.y,
                        onAnimationEnd = {
                            floatingTexts = floatingTexts.filter { it.id != floatingText.id }
                        }
                    )
                }
            }
            
            // Tab Layout
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF424242),
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color(0xFF4CAF50) else Color.White
                            )
                        }
                    )
                }
            }
            
            // Tab Content
            when (selectedTab) {
                0 -> AutomationToolsTab(
                    tools = uiState.automationTools,
                    userCash = uiState.cash,
                    onPurchase = { toolId -> viewModel.purchaseAutomationTool(toolId) }
                )
                1 -> PremiumToolsScreen()
            }
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
fun EnhancedStatsPanel(
    cash: Long,
    coins: Int,
    moneyPerSecond: Double,
    moneyFlashAlpha: Float,
    onShopClick: () -> Unit,
    onExchangeClick: () -> Unit
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
                    Box {
                        Text(
                            text = NumberFormatter.formatMoney(cash),
                            color = Color(0xFF4CAF50),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // Flash overlay
                        Text(
                            text = NumberFormatter.formatMoney(cash),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alpha(moneyFlashAlpha)
                        )
                    }
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
                                text = "Shop",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Income: ${NumberFormatter.formatRate(moneyPerSecond)}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                
                Button(
                    onClick = onExchangeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Exchange",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedFactoryClickButton(
    isAnimating: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "button_scale"
    )
    
    // Sparkle animation
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotation"
    )
    
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        // Sparkle effects
        if (isAnimating) {
            repeat(6) { index ->
                val angle = (index * 60f) + sparkleRotation
                val radius = 90.dp
                Box(
                    modifier = Modifier
                        .offset(
                            x = (radius.value * kotlin.math.cos(Math.toRadians(angle.toDouble()))).dp,
                            y = (radius.value * kotlin.math.sin(Math.toRadians(angle.toDouble()))).dp
                        )
                        .size(8.dp)
                        .background(Color(0xFFFFD700), CircleShape)
                )
            }
        }
        
        // Main button
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
}

@Composable
fun FloatingMoneyText(
    text: String,
    startX: Float,
    startY: Float,
    onAnimationEnd: () -> Unit
) {
    val animatedY = remember { Animatable(startY) }
    val animatedAlpha = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        // Animate upward movement and fade out concurrently
        launch {
            animatedY.animateTo(-100f, animationSpec = tween(1000))
        }
        launch {
            delay(500)
            animatedAlpha.animateTo(0f, animationSpec = tween(500))
            onAnimationEnd()
        }
    }
    
    Text(
        text = text,
        color = Color(0xFF4CAF50),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .offset(x = startX.dp, y = animatedY.value.dp)
            .alpha(animatedAlpha.value)
    )
}

@Composable
fun AutomationToolsTab(
    tools: List<com.terminal3.demo.money_printer.data.models.AutomationTool>,
    userCash: Long,
    onPurchase: (String) -> Unit
) {
    com.terminal3.demo.money_printer.ui.main.AutomationToolsList(
        tools = tools,
        userCash = userCash,
        onPurchase = onPurchase
    )
}

data class FloatingText(
    val text: String,
    val x: Float,
    val y: Float,
    val id: Long
)