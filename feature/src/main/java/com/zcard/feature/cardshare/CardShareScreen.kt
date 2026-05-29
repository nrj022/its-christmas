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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.feature.R

@Composable
fun CardShareScreen(cardUrl: String, onBackClicked: () -> Unit = {}, onHomeClicked: () -> Unit = {}, onShareClicked: () -> Unit = {}) {
    var isLoading by remember { mutableStateOf(true) }

    CardShareContent(
        cardUrl = cardUrl,
        isLoading = isLoading,
        onPageFinished = { isLoading = false },
        onBackClicked = onBackClicked,
        onHomeClicked = onHomeClicked,
        onShareClicked = onShareClicked
    )
}

@Composable
private fun CardShareContent(
    cardUrl: String = "",
    isLoading: Boolean = false,
    onPageFinished: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onShareClicked: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {

        CardShareWebView(cardUrl, isLoading, onPageFinished)

        TopSection(onBackClicked, onHomeClicked)

        ShareButton(onShareClicked)
    }
}

@Composable
private fun TopSection(onBackClicked: () -> Unit, onHomeClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 10.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackButton(onBackClicked)

        HomeButton(onHomeClicked)
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
private fun CardShareWebView(cardUrl: String, isLoading: Boolean, onPageFinished: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                val webView = WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            onPageFinished()
                            println("onPageFinished: $url")
                        }
                    }
                }

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
                    webView.loadUrl(cardUrl)
                }
            },
            onRelease = { webView ->
                webView.clearHistory()
                webView.clearCache(true)
                webView.stopLoading()   // 진행중인 동작 중단
                webView.destroy()
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Gray
            )
        }
    }
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
