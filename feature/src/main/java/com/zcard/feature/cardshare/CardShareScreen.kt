package com.zcard.feature.cardshare

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.feature.R

@Composable
fun CardShareScreen(viewModel:CardShareViewModel = hiltViewModel(), cardUrl: String) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    CardShareContent(
        cardUrl = cardUrl,
        isLoading = state.value.isLoading,
        isOnline = state.value.isOnline,
        onNetworkCheckAgainClicked = { viewModel.onIntent(CardShareIntent.CheckNetwork) },
        onPageFinished = { viewModel.onIntent(CardShareIntent.StopLoading) },
        onBackClicked = { viewModel.onIntent(CardShareIntent.NavigateBack) },
        onHomeClicked = { viewModel.onIntent(CardShareIntent.NavigateToHome) },
        onShareClicked = { viewModel.onIntent(CardShareIntent.ShareCardLink(cardUrl)) }
    )
}

@Composable
private fun CardShareContent(
    cardUrl: String = "",
    isLoading: Boolean = false,
    isOnline: Boolean = false,
    onNetworkCheckAgainClicked: () -> Unit = {},
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
        if(isOnline) {
            CardShareWebView(cardUrl, isLoading, onPageFinished)
        } else {
            NoNetworkScreen(onNetworkCheckAgainClicked)
        }

        TopSection(onBackClicked, onHomeClicked)

        ShareButton(onShareClicked)
    }
}


@Composable
private fun NoNetworkScreen(onTryAgainClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.share_text_no_internet),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(modifier = Modifier.height(25.dp))
        TextButton (
            onClick = onTryAgainClicked,
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = SoftBlack),
        ) {
            Text(
                text = stringResource(R.string.share_button_try_again),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
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

@SuppressLint("SetJavaScriptEnabled")
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
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            if(request?.isForMainFrame == true) onPageFinished()
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
