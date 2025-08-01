package com.terminal3.demo.money_printer.utils

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

object NumberFormatter {
    private val suffixes = arrayOf("", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc")
    
    fun formatMoney(amount: Long): String {
        if (amount == 0L) return "$0"
        
        val absAmount = abs(amount)
        if (absAmount < 1000) return "$$amount"
        
        val magnitude = (log10(absAmount.toDouble()) / 3).toInt()
        val scaledAmount = absAmount / 10.0.pow(magnitude * 3)
        
        val suffix = if (magnitude < suffixes.size) suffixes[magnitude] else "e${magnitude * 3}"
        
        return when {
            scaledAmount >= 100 -> "$${scaledAmount.toInt()}$suffix"
            scaledAmount >= 10 -> "$${String.format("%.1f", scaledAmount)}$suffix"
            else -> "$${String.format("%.2f", scaledAmount)}$suffix"
        }
    }
    
    fun formatNumber(amount: Long): String {
        if (amount == 0L) return "0"
        
        val absAmount = abs(amount)
        if (absAmount < 1000) return amount.toString()
        
        val magnitude = (log10(absAmount.toDouble()) / 3).toInt()
        val scaledAmount = absAmount / 10.0.pow(magnitude * 3)
        
        val suffix = if (magnitude < suffixes.size) suffixes[magnitude] else "e${magnitude * 3}"
        
        return when {
            scaledAmount >= 100 -> "${scaledAmount.toInt()}$suffix"
            scaledAmount >= 10 -> "${String.format("%.1f", scaledAmount)}$suffix"
            else -> "${String.format("%.2f", scaledAmount)}$suffix"
        }
    }
    
    fun formatRate(rate: Double): String {
        if (rate == 0.0) return "0/sec"
        if (rate < 1.0) return "${String.format("%.2f", rate)}/sec"
        return "${formatNumber(rate.toLong())}/sec"
    }
}