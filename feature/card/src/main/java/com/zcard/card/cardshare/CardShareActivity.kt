package com.zcard.card.cardshare

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.zcard.designsystem.theme.ZCardTheme

class CardShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cardUrl = intent.getStringExtra("cardUrl") ?: ""

        setContent {
            ZCardTheme {
                CardShareScreen(
                    cardUrl = cardUrl,
                    onBackClicked = ::finish,
                    onHomeClicked = ::navigationToMain,
                    onShareClicked = { shareCardLink(cardUrl) }
                )
            }
        }
    }

    private fun navigationToMain() {
        val intent = Intent(Intent.ACTION_VIEW, "zcard://main".toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun shareCardLink(cardUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, cardUrl)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, null))
    }
}