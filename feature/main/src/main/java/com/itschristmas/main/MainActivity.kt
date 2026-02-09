package com.itschristmas.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.itschristmas.card.cardeditor.CardEditorActivity
import com.itschristmas.designsystem.theme.ItsChristmasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItsChristmasTheme {
                MainScreen { id ->
                    val intent = Intent().apply {
                        setClass(this@MainActivity, CardEditorActivity::class.java)
                        putExtra("cardId", id)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadCards()
    }
}