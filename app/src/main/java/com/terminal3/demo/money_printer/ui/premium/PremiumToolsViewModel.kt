package com.terminal3.demo.money_printer.ui.premium

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.terminal3.demo.money_printer.data.models.PremiumItem
import com.terminal3.demo.money_printer.data.models.PremiumItems
import com.terminal3.demo.money_printer.data.repositories.GameRepository
import com.terminal3.demo.money_printer.game.engine.GameEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PremiumToolsViewModel(application: Application) : AndroidViewModel(application) {
    private val gameRepository = GameRepository(application)
    private val gameEngine = GameEngine()
    
    private val _uiState = MutableStateFlow(PremiumToolsUiState())
    val uiState: StateFlow<PremiumToolsUiState> = _uiState.asStateFlow()
    
    init {
        loadGameState()
        startBoostUpdater()
    }
    
    private fun loadGameState() {
        viewModelScope.launch {
            gameRepository.gameState.collect { gameState ->
                gameEngine.updateGameState(gameState)
                updateUiState()
            }
        }
    }
    
    private fun startBoostUpdater() {
        viewModelScope.launch {
            while (true) {
                gameEngine.updateBoosts()
                updateUiState()
                delay(1000) // Update every second
            }
        }
    }
    
    private fun updateUiState() {
        val gameState = gameEngine.getGameState()
        val premiumManager = gameEngine.getPremiumItemManager()
        
        viewModelScope.launch {
            premiumManager.ownedItems.collect { ownedItems ->
                _uiState.value = _uiState.value.copy(
                    currentCoins = gameState.coins,
                    ownedItems = ownedItems,
                    activeBoosts = gameEngine.getActiveBoostInfo()
                )
            }
        }
    }
    
    fun purchaseItem(itemId: String, coinCost: Int) {
        if (gameEngine.purchasePremiumItem(itemId, coinCost)) {
            gameRepository.saveGameState(gameEngine.getGameState())
            updateUiState()
        }
    }
}

data class PremiumToolsUiState(
    val currentCoins: Int = 0,
    val premiumItems: List<PremiumItem> = PremiumItems.ALL_ITEMS,
    val ownedItems: Set<String> = emptySet(),
    val activeBoosts: List<Pair<String, Long>> = emptyList()
)