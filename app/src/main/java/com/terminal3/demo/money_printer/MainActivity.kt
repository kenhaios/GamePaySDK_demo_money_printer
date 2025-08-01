package com.terminal3.demo.money_printer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.terminal3.demo.money_printer.ui.navigation.MoneyFactoryApp
import com.terminal3.demo.money_printer.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MoneyFactoryApp()
            }
        }
    }
}