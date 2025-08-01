package com.terminal3.demo.money_printer.ui.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.terminal3.demo.money_printer.data.models.CoinPackage
import com.terminal3.demo.money_printer.data.models.CoinPackages
import com.terminal3.demo.money_printer.data.repositories.GameRepository
import com.terminal3.demo.money_printer.payment.PaymentCallback
import com.terminal3.demo.money_printer.payment.PaymentManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoinShopViewModel(application: Application) : AndroidViewModel(application) {
    private val gameRepository = GameRepository(application)
    private val paymentManager = PaymentManager(application)
    
    private val _uiState = MutableStateFlow(CoinShopUiState())
    val uiState: StateFlow<CoinShopUiState> = _uiState.asStateFlow()
    
    init {
        paymentManager.initializeSDK()
        loadCurrentCoins()
    }
    
    private fun loadCurrentCoins() {
        viewModelScope.launch {
            gameRepository.gameState.collect { gameState ->
                _uiState.value = _uiState.value.copy(currentCoins = gameState.coins)
            }
        }
    }
    
    fun purchaseCoinPackage(coinPackage: CoinPackage) {
        if (_uiState.value.isLoading) return
        
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            selectedPackageId = coinPackage.id
        )
        
        paymentManager.purchaseCoins(coinPackage, object : PaymentCallback {
            override fun onSuccess(transactionId: String, packageId: String) {
                viewModelScope.launch {
                    // Award coins to the user
                    val currentState = gameRepository.gameState.value
                    val coinsToAdd = coinPackage.coinAmount + getBonusCoins(coinPackage)
                    val newState = currentState.copy(coins = currentState.coins + coinsToAdd)
                    
                    gameRepository.saveGameState(newState)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedPackageId = null,
                        message = "Purchase successful! You received $coinsToAdd coins!"
                    )
                }
            }
            
            override fun onError(error: String) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedPackageId = null,
                    message = "Purchase failed: $error"
                )
            }
            
            override fun onCancel() {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedPackageId = null,
                    message = "Purchase cancelled"
                )
            }
        })
    }
    
    private fun getBonusCoins(coinPackage: CoinPackage): Int {
        return when (coinPackage.id) {
            "medium" -> 50
            "large" -> 200
            "mega" -> 500
            "ultimate" -> 1000
            else -> 0
        }
    }
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class CoinShopUiState(
    val currentCoins: Int = 0,
    val coinPackages: List<CoinPackage> = CoinPackages.ALL_PACKAGES,
    val isLoading: Boolean = false,
    val selectedPackageId: String? = null,
    val message: String? = null
)