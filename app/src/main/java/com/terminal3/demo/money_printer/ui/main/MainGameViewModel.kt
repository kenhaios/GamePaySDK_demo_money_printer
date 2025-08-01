package com.terminal3.demo.money_printer.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.terminal3.demo.money_printer.data.models.AutomationTool
import com.terminal3.demo.money_printer.data.models.GameState
import com.terminal3.demo.money_printer.data.repositories.GameRepository
import com.terminal3.demo.money_printer.game.engine.GameEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainGameViewModel(application: Application) : AndroidViewModel(application) {
    private val gameRepository = GameRepository(application)
    private val gameEngine = GameEngine()
    
    private val _uiState = MutableStateFlow(MainGameUiState())
    val uiState: StateFlow<MainGameUiState> = _uiState.asStateFlow()
    
    private val _moneyPerSecond = MutableStateFlow(0.0)
    val moneyPerSecond: StateFlow<Double> = _moneyPerSecond.asStateFlow()
    
    private val _clickAnimation = MutableStateFlow(false)
    val clickAnimation: StateFlow<Boolean> = _clickAnimation.asStateFlow()
    
    init {
        initializeGame()
        startGameLoop()
    }
    
    private fun initializeGame() {
        // Load premium data first
        val (ownedItems, activeBoosts) = gameRepository.loadPremiumData()
        gameEngine.loadPremiumData(ownedItems, activeBoosts)
        
        viewModelScope.launch {
            gameRepository.gameState.collect { savedState ->
                gameEngine.updateGameState(savedState)
                updateUiState()
                
                val offlineTime = gameRepository.getOfflineTime()
                if (offlineTime > 60000) { // More than 1 minute offline
                    val offlineEarnings = gameEngine.calculateOfflineEarnings(offlineTime)
                    if (offlineEarnings > 0) {
                        _uiState.value = _uiState.value.copy(
                            showOfflineEarnings = true,
                            offlineEarningsAmount = offlineEarnings
                        )
                    }
                }
            }
        }
    }
    
    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                val moneyPerSec = gameEngine.calculateMoneyPerSecond()
                if (moneyPerSec > 0) {
                    val currentState = gameEngine.getGameState()
                    val increment = (moneyPerSec / 10.0).toLong() // Update 10 times per second
                    if (increment > 0) {
                        val newState = currentState.copy(
                            cash = currentState.cash + increment,
                            totalEarned = currentState.totalEarned + increment
                        )
                        gameEngine.updateGameState(newState)
                        gameRepository.updateGameState(newState)
                        updateUiState()
                    }
                }
                _moneyPerSecond.value = moneyPerSec
                delay(100) // Update 10 times per second
            }
        }
    }
    
    fun onMoneyClick() {
        val earnedAmount = gameEngine.processClick()
        gameRepository.updateGameState(gameEngine.getGameState())
        updateUiState()
        
        // Trigger click animation
        _clickAnimation.value = true
        viewModelScope.launch {
            delay(200)
            _clickAnimation.value = false
        }
    }
    
    fun purchaseAutomationTool(toolId: String) {
        if (gameEngine.purchaseAutomation(toolId)) {
            gameRepository.saveGameState(gameEngine.getGameState())
            updateUiState()
        }
    }
    
    fun dismissOfflineEarnings() {
        _uiState.value = _uiState.value.copy(showOfflineEarnings = false)
    }
    
    private fun updateUiState() {
        val gameState = gameEngine.getGameState()
        val automationTools = gameEngine.getAllAutomationTools()
        
        _uiState.value = _uiState.value.copy(
            cash = gameState.cash,
            coins = gameState.coins,
            totalEarned = gameState.totalEarned,
            automationTools = automationTools
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        gameRepository.saveGameState(gameEngine.getGameState())
        val (ownedItems, activeBoosts) = gameEngine.savePremiumData()
        gameRepository.savePremiumData(ownedItems, activeBoosts)
    }
}

data class MainGameUiState(
    val cash: Long = 0,
    val coins: Int = 0,
    val totalEarned: Long = 0,
    val automationTools: List<AutomationTool> = emptyList(),
    val showOfflineEarnings: Boolean = false,
    val offlineEarningsAmount: Long = 0
)