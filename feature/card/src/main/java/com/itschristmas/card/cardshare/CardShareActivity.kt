package com.itschristmas.card.cardshare

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.itschristmas.designsystem.theme.ItsChristmasTheme

class CardShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cardUrl = intent.getStringExtra("cardUrl") ?: ""

        setContent {
            ItsChristmasTheme {
                CardShareScreen(cardUrl)
            }
        }
    }
}