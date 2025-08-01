package com.terminal3.demo.money_printer.game.premium

import com.terminal3.demo.money_printer.data.models.ActiveBoost
import com.terminal3.demo.money_printer.data.models.PremiumItem
import com.terminal3.demo.money_printer.data.models.PremiumItemType
import com.terminal3.demo.money_printer.data.models.PremiumItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PremiumItemManager {
    private val _ownedItems = MutableStateFlow<Set<String>>(emptySet())
    val ownedItems: StateFlow<Set<String>> = _ownedItems.asStateFlow()
    
    private val _activeBoosts = MutableStateFlow<List<ActiveBoost>>(emptyList())
    val activeBoosts: StateFlow<List<ActiveBoost>> = _activeBoosts.asStateFlow()
    
    fun purchaseItem(itemId: String): Boolean {
        val item = PremiumItems.ALL_ITEMS.find { it.id == itemId } ?: return false
        
        when (item.type) {
            PremiumItemType.PERMANENT_AUTOMATION_MULTIPLIER,
            PremiumItemType.PERMANENT_CLICK_MULTIPLIER -> {
                // Add to owned items for permanent effects
                val currentOwned = _ownedItems.value.toMutableSet()
                currentOwned.add(itemId)
                _ownedItems.value = currentOwned
            }
            PremiumItemType.TEMPORARY_PRODUCTION_BOOST,
            PremiumItemType.TEMPORARY_AUTO_COLLECT,
            PremiumItemType.TEMPORARY_MEGA_BOOST -> {
                // Add to active boosts
                val endTime = System.currentTimeMillis() + (item.durationMinutes * 60 * 1000L)
                val boost = ActiveBoost(itemId, item.type, item.effectValue, endTime)
                val currentBoosts = _activeBoosts.value.toMutableList()
                
                // Remove existing boost of same type to prevent stacking
                currentBoosts.removeAll { it.type == item.type }
                currentBoosts.add(boost)
                _activeBoosts.value = currentBoosts
            }
        }
        return true
    }
    
    fun updateActiveBoosts() {
        val currentTime = System.currentTimeMillis()
        val validBoosts = _activeBoosts.value.filter { it.endTime > currentTime }
        
        if (validBoosts.size != _activeBoosts.value.size) {
            _activeBoosts.value = validBoosts
        }
    }
    
    fun getAutomationMultiplier(): Double {
        var multiplier = 1.0
        
        // Permanent multipliers
        if (_ownedItems.value.contains("super_printer")) {
            multiplier *= 2.0
        }
        
        // Temporary boosts
        updateActiveBoosts()
        _activeBoosts.value.forEach { boost ->
            when (boost.type) {
                PremiumItemType.TEMPORARY_PRODUCTION_BOOST -> multiplier *= boost.effectValue
                PremiumItemType.TEMPORARY_MEGA_BOOST -> multiplier *= boost.effectValue
                else -> {}
            }
        }
        
        return multiplier
    }
    
    fun getClickMultiplier(): Double {
        var multiplier = 1.0
        
        // Permanent multipliers
        if (_ownedItems.value.contains("golden_touch")) {
            multiplier *= 10.0
        }
        
        // Temporary boosts don't typically affect clicks, but mega boost could
        updateActiveBoosts()
        _activeBoosts.value.forEach { boost ->
            when (boost.type) {
                PremiumItemType.TEMPORARY_MEGA_BOOST -> multiplier *= boost.effectValue
                else -> {}
            }
        }
        
        return multiplier
    }
    
    fun hasAutoCollectBoost(): Boolean {
        updateActiveBoosts()
        return _activeBoosts.value.any { it.type == PremiumItemType.TEMPORARY_AUTO_COLLECT }
    }
    
    fun getAutoCollectRate(): Double {
        updateActiveBoosts()
        val autoCollectBoost = _activeBoosts.value.find { it.type == PremiumItemType.TEMPORARY_AUTO_COLLECT }
        return autoCollectBoost?.effectValue ?: 0.0
    }
    
    fun getActiveBoostInfo(): List<Pair<String, Long>> {
        updateActiveBoosts()
        return _activeBoosts.value.map { boost ->
            val item = PremiumItems.ALL_ITEMS.find { it.id == boost.itemId }
            val timeLeft = boost.endTime - System.currentTimeMillis()
            (item?.name ?: "Unknown") to maxOf(0L, timeLeft)
        }
    }
    
    fun isItemOwned(itemId: String): Boolean {
        val item = PremiumItems.ALL_ITEMS.find { it.id == itemId } ?: return false
        
        return when (item.type) {
            PremiumItemType.PERMANENT_AUTOMATION_MULTIPLIER,
            PremiumItemType.PERMANENT_CLICK_MULTIPLIER -> {
                _ownedItems.value.contains(itemId)
            }
            else -> false // Temporary items can always be purchased again
        }
    }
    
    fun loadFromData(ownedItems: Set<String>, activeBoosts: List<ActiveBoost>) {
        _ownedItems.value = ownedItems
        _activeBoosts.value = activeBoosts.filter { it.endTime > System.currentTimeMillis() }
    }
    
    fun getSaveData(): Pair<Set<String>, List<ActiveBoost>> {
        updateActiveBoosts()
        return _ownedItems.value to _activeBoosts.value
    }
}