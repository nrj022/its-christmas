package com.itschristmas.card.cardshare

import android.content.Intent
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
                CardShareScreen(
                    cardUrl = cardUrl,
                    onShareClicked = { shareCardLink(cardUrl) }
                )
            }
        }

    private fun shareCardLink(cardUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, cardUrl)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, null))
    }
}