package com.terminal3.demo.money_printer.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.terminal3.demo.money_printer.data.models.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_state", Context.MODE_PRIVATE)
    
    private val _gameState = MutableStateFlow(loadGameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private fun loadGameState(): GameState {
        val cash = prefs.getLong("cash", 0L)
        val coins = prefs.getInt("coins", 0)
        val totalEarned = prefs.getLong("total_earned", 0L)
        val lastSaveTime = prefs.getLong("last_save_time", System.currentTimeMillis())
        
        val automationTools = mutableMapOf<String, Int>()
        val toolKeys = prefs.getStringSet("automation_tool_keys", emptySet()) ?: emptySet()
        
        for (key in toolKeys) {
            automationTools[key] = prefs.getInt("tool_$key", 0)
        }
        
        return GameState(
            cash = cash,
            coins = coins,
            automationTools = automationTools.toMap(),
            totalEarned = totalEarned,
            lastSaveTime = lastSaveTime
        )
    }
    
    fun saveGameState(gameState: GameState) {
        val editor = prefs.edit()
        editor.putLong("cash", gameState.cash)
        editor.putInt("coins", gameState.coins)
        editor.putLong("total_earned", gameState.totalEarned)
        editor.putLong("last_save_time", System.currentTimeMillis())
        
        val toolKeys = gameState.automationTools.keys
        editor.putStringSet("automation_tool_keys", toolKeys)
        
        for ((toolId, count) in gameState.automationTools) {
            editor.putInt("tool_$toolId", count)
        }
        
        editor.apply()
        
        _gameState.value = gameState.copy(lastSaveTime = System.currentTimeMillis())
    }
    
    fun updateGameState(gameState: GameState) {
        _gameState.value = gameState
    }
    
    fun getOfflineTime(): Long {
        val currentTime = System.currentTimeMillis()
        val lastSaveTime = prefs.getLong("last_save_time", currentTime)
        return currentTime - lastSaveTime
    }
}