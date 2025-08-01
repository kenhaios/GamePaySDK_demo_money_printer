package com.terminal3.demo.money_printer.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.terminal3.demo.money_printer.data.models.ActiveBoost
import com.terminal3.demo.money_printer.data.models.GameState
import com.terminal3.demo.money_printer.data.models.PremiumItemType
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
    
    fun savePremiumData(ownedItems: Set<String>, activeBoosts: List<ActiveBoost>) {
        val editor = prefs.edit()
        
        // Save owned items
        editor.putStringSet("premium_owned_items", ownedItems)
        
        // Save active boosts
        editor.putInt("active_boosts_count", activeBoosts.size)
        activeBoosts.forEachIndexed { index, boost ->
            editor.putString("boost_${index}_id", boost.itemId)
            editor.putString("boost_${index}_type", boost.type.name)
            editor.putFloat("boost_${index}_value", boost.effectValue.toFloat())
            editor.putLong("boost_${index}_end", boost.endTime)
        }
        
        editor.apply()
    }
    
    fun loadPremiumData(): Pair<Set<String>, List<ActiveBoost>> {
        val ownedItems = prefs.getStringSet("premium_owned_items", emptySet()) ?: emptySet()
        
        val boostCount = prefs.getInt("active_boosts_count", 0)
        val activeBoosts = mutableListOf<ActiveBoost>()
        
        for (i in 0 until boostCount) {
            val itemId = prefs.getString("boost_${i}_id", "") ?: ""
            val typeName = prefs.getString("boost_${i}_type", "") ?: ""
            val value = prefs.getFloat("boost_${i}_value", 0f).toDouble()
            val endTime = prefs.getLong("boost_${i}_end", 0L)
            
            if (itemId.isNotEmpty() && typeName.isNotEmpty()) {
                try {
                    val type = PremiumItemType.valueOf(typeName)
                    if (endTime > System.currentTimeMillis()) { // Only load non-expired boosts
                        activeBoosts.add(ActiveBoost(itemId, type, value, endTime))
                    }
                } catch (e: IllegalArgumentException) {
                    // Invalid type name, skip this boost
                }
            }
        }
        
        return ownedItems to activeBoosts
    }
}