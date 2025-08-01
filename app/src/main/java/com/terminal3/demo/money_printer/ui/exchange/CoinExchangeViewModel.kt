package com.terminal3.demo.money_printer.ui.exchange

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.terminal3.demo.money_printer.data.models.CoinExchangeOption
import com.terminal3.demo.money_printer.data.models.CoinExchangeOptions
import com.terminal3.demo.money_printer.data.repositories.GameRepository
import com.terminal3.demo.money_printer.game.engine.GameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoinExchangeViewModel(application: Application) : AndroidViewModel(application) {
    private val gameRepository = GameRepository(application)
    private val gameEngine = GameEngine()
    
    private val _uiState = MutableStateFlow(CoinExchangeUiState())
    val uiState: StateFlow<CoinExchangeUiState> = _uiState.asStateFlow()
    
    init {
        loadGameState()
    }
    
    private fun loadGameState() {
        viewModelScope.launch {
            gameRepository.gameState.collect { gameState ->
                gameEngine.updateGameState(gameState)
                _uiState.value = _uiState.value.copy(
                    currentCoins = gameState.coins,
                    currentCash = gameState.cash
                )
            }
        }
    }
    
    fun exchangeCoins(option: CoinExchangeOption) {
        if (gameEngine.exchangeCoinsForCash(option.coinsRequired, option.cashReceived)) {
            gameRepository.saveGameState(gameEngine.getGameState())
            
            _uiState.value = _uiState.value.copy(
                message = "Successfully exchanged ${option.coinsRequired} coins for ${option.cashReceived} cash!"
            )
        }
    }
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class CoinExchangeUiState(
    val currentCoins: Int = 0,
    val currentCash: Long = 0,
    val exchangeOptions: List<CoinExchangeOption> = CoinExchangeOptions.ALL_OPTIONS,
    val message: String? = null
)