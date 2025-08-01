package com.terminal3.demo.money_printer.game.engine

import com.terminal3.demo.money_printer.data.models.AutomationTool
import com.terminal3.demo.money_printer.data.models.AutomationTools
import com.terminal3.demo.money_printer.data.models.GameState
import com.terminal3.demo.money_printer.game.premium.PremiumItemManager

class GameEngine {
    private var gameState = GameState()
    private val automationTools = AutomationTools.ALL_TOOLS.map { it.copy() }.toMutableList()
    private val premiumItemManager = PremiumItemManager()
    
    fun getGameState(): GameState = gameState
    
    fun updateGameState(newState: GameState) {
        gameState = newState
        syncAutomationTools()
    }
    
    fun loadPremiumData(ownedItems: Set<String>, activeBoosts: List<com.terminal3.demo.money_printer.data.models.ActiveBoost>) {
        premiumItemManager.loadFromData(ownedItems, activeBoosts)
    }
    
    fun savePremiumData(): Pair<Set<String>, List<com.terminal3.demo.money_printer.data.models.ActiveBoost>> {
        return premiumItemManager.getSaveData()
    }
    
    private fun syncAutomationTools() {
        automationTools.forEach { tool ->
            tool.owned = gameState.automationTools[tool.id] ?: 0
        }
    }
    
    fun calculateMoneyPerSecond(): Double {
        val baseProduction = automationTools.sumOf { it.getCurrentProduction() }
        val multiplier = premiumItemManager.getAutomationMultiplier()
        return baseProduction * multiplier
    }
    
    fun processClick(): Long {
        val baseClickValue = 1L
        val bonusMultiplier = calculateClickMultiplier()
        val clickValue = (baseClickValue * bonusMultiplier).toLong()
        
        gameState = gameState.copy(
            cash = gameState.cash + clickValue,
            totalEarned = gameState.totalEarned + clickValue
        )
        
        return clickValue
    }
    
    private fun calculateClickMultiplier(): Double {
        val baseMultiplier = 1.0 + (automationTools.sumOf { it.owned } * 0.1)
        val premiumMultiplier = premiumItemManager.getClickMultiplier()
        return baseMultiplier * premiumMultiplier
    }
    
    fun calculateOfflineEarnings(offlineTime: Long): Long {
        val maxOfflineTime = 4L * 60L * 60L * 1000L // 4 hours in milliseconds
        val actualOfflineTime = kotlin.math.min(offlineTime, maxOfflineTime)
        val moneyPerSecond = calculateMoneyPerSecond()
        val offlineEarnings = (moneyPerSecond * (actualOfflineTime.toDouble() / 1000.0)).toLong()
        
        if (offlineEarnings > 0) {
            gameState = gameState.copy(
                cash = gameState.cash + offlineEarnings,
                totalEarned = gameState.totalEarned + offlineEarnings
            )
        }
        
        return offlineEarnings
    }
    
    fun purchaseAutomation(toolId: String): Boolean {
        val tool = automationTools.find { it.id == toolId } ?: return false
        val cost = tool.getCurrentCost()
        
        if (gameState.cash >= cost) {
            tool.owned++
            gameState = gameState.copy(
                cash = gameState.cash - cost,
                automationTools = gameState.automationTools + (toolId to tool.owned)
            )
            return true
        }
        return false
    }
    
    fun getAutomationTool(toolId: String): AutomationTool? {
        return automationTools.find { it.id == toolId }
    }
    
    fun getAllAutomationTools(): List<AutomationTool> = automationTools
    
    fun canAfford(toolId: String): Boolean {
        val tool = automationTools.find { it.id == toolId } ?: return false
        return gameState.cash >= tool.getCurrentCost()
    }
    
    fun addCoins(amount: Int) {
        gameState = gameState.copy(coins = gameState.coins + amount)
    }
    
    fun spendCoins(amount: Int): Boolean {
        if (gameState.coins >= amount) {
            gameState = gameState.copy(coins = gameState.coins - amount)
            return true
        }
        return false
    }
    
    fun purchasePremiumItem(itemId: String, coinCost: Int): Boolean {
        if (gameState.coins >= coinCost && !premiumItemManager.isItemOwned(itemId)) {
            if (premiumItemManager.purchaseItem(itemId)) {
                gameState = gameState.copy(coins = gameState.coins - coinCost)
                return true
            }
        }
        return false
    }
    
    fun exchangeCoinsForCash(coins: Int, cashAmount: Long): Boolean {
        if (gameState.coins >= coins) {
            gameState = gameState.copy(
                coins = gameState.coins - coins,
                cash = gameState.cash + cashAmount,
                totalEarned = gameState.totalEarned + cashAmount
            )
            return true
        }
        return false
    }
    
    fun getPremiumItemManager(): PremiumItemManager = premiumItemManager
    
    fun getActiveBoostInfo(): List<Pair<String, Long>> = premiumItemManager.getActiveBoostInfo()
    
    fun updateBoosts() {
        premiumItemManager.updateActiveBoosts()
    }
}