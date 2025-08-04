package com.terminal3.demo.money_printer.flappy

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class FlappyUiState(
    val coins: Int = 0,
    val selectedBird: String = "classic",
    val selectedTheme: String = "day",
    val ownedBirds: Set<String> = setOf("classic"),
    val ownedThemes: Set<String> = setOf("day")
)

data class ShopItem(
    val id: String,
    val name: String,
    val cost: Int,
    val type: ItemType
)

enum class ItemType { BIRD, THEME }

class FlappyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FlappyUiState())
    val uiState: StateFlow<FlappyUiState> = _uiState

    fun addCoins(amount: Int) {
        _uiState.update { it.copy(coins = it.coins + amount) }
    }

    fun purchase(item: ShopItem) {
        _uiState.update { state ->
            if (state.coins < item.cost) return@update state
            when (item.type) {
                ItemType.BIRD -> state.copy(
                    coins = state.coins - item.cost,
                    ownedBirds = state.ownedBirds + item.id,
                    selectedBird = item.id
                )
                ItemType.THEME -> state.copy(
                    coins = state.coins - item.cost,
                    ownedThemes = state.ownedThemes + item.id,
                    selectedTheme = item.id
                )
            }
        }
    }

    fun select(item: ShopItem) {
        _uiState.update { state ->
            when (item.type) {
                ItemType.BIRD -> if (item.id in state.ownedBirds) state.copy(selectedBird = item.id) else state
                ItemType.THEME -> if (item.id in state.ownedThemes) state.copy(selectedTheme = item.id) else state
            }
        }
    }
}
