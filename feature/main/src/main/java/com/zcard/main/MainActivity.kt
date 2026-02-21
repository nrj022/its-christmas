package com.zcard.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.zcard.designsystem.theme.ZCardTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZCardTheme {
                MainScreen { id ->
                    val intent = Intent(Intent.ACTION_VIEW, "zcard://card/editor".toUri())
                    intent.putExtra("cardId", id)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCards()
    }
}