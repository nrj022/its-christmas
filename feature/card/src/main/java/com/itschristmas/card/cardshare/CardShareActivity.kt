package com.itschristmas.card.cardshare

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.itschristmas.designsystem.theme.ItsChristmasTheme

class CardShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cardUrl = intent.getStringExtra("cardUrl") ?: ""

        setContent {
            ItsChristmasTheme {
                CardShareScreen(
                    cardUrl = cardUrl,
                    onBackClicked = ::finish,
                    onCompleteClicked = ::navigationToMain,
                    onShareClicked = { shareCardLink(cardUrl) }
                )
            }
        }
    }

    private fun navigationToMain() {
        val intent = Intent(Intent.ACTION_VIEW, "itschristmas://main".toUri())
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