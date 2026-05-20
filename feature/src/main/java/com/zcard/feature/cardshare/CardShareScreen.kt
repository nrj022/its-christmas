package com.zcard.feature.cardshare

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.feature.R

@Composable
fun CardShareScreen(cardUrl: String, onBackClicked: () -> Unit = {}, onHomeClicked: () -> Unit = {}, onShareClicked: () -> Unit = {}) {
    CardShareContent(
        cardUrl = cardUrl,
        onBackClicked = onBackClicked,
        onHomeClicked = onHomeClicked,
        onShareClicked = onShareClicked
    )
}

@Composable
private fun CardShareContent(
    cardUrl: String = "",
    onBackClicked: () -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onShareClicked: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {

        CardShareWebView(cardUrl)

        TopSection(onBackClicked, onHomeClicked)

        ShareButton(onShareClicked)
    }
}

@Composable
private fun TopSection(onBackClicked: () -> Unit, onCompleteClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackButton(onBackClicked)

        HomeButton(onCompleteClicked)
    }
}

@Composable
private fun BackButton(onBackClicked: () -> Unit) {
    IconButton(
        onClick = onBackClicked,
        modifier = Modifier.background(White.copy(alpha = 0.7f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_cd_back_button),
            tint = SoftBlack
        )
    }
}

@Composable
private fun HomeButton(onHomeClicked: () -> Unit) {
    IconButton(
        onClick = onHomeClicked,
        modifier = Modifier.background(White.copy(alpha = 0.7f), CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home),
            contentDescription = stringResource(R.string.share_cd_home_button),
            tint = SoftBlack
        )
    }
}

@Composable
private fun CardShareWebView(cardUrl: String) {
    AndroidView(
        factory = { context ->
            val webView = WebView(context)
            webView.webViewClient = WebViewClient()

            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadsImagesAutomatically = true
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = false
            }

            webView.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }, update = { webView ->
            webView.loadUrl(cardUrl)
        },
        onRelease = { webView ->
            webView.clearHistory()
            webView.clearCache(true)
            webView.stopLoading()   // 진행중인 동작 중단
            webView.destroy()
        }
    )
}

@Composable
private fun BoxScope.ShareButton(onShareClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 36.dp)
            .align(Alignment.BottomCenter),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onShareClicked,
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = SoftBlack),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = null,
                    tint = SoftBlack
                )
                Text(
                    text = stringResource(R.string.share_button_share),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun CardShareScreenPreview() {
    CardShareContent()
}
