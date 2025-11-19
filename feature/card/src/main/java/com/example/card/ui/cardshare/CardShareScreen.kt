package com.example.card.ui.cardshare

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.card.R
import com.example.designsystem.theme.SoftBlack
import com.example.designsystem.theme.White

@Composable
fun CardShareScreen(
    onBackClicked: () -> Unit = {},
    onCompleteClicked: () -> Unit = {},
    onShareClicked: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {

        WebView()

        TopSection(onBackClicked, onCompleteClicked)

        ShareButton(onShareClicked)
    }
}

@Composable
fun TopSection(onBackClicked: () -> Unit, onCompleteClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackButton(onBackClicked)

        CompleteButton(onCompleteClicked)
    }
}

@Composable
fun BackButton(onBackClicked: () -> Unit) {
    IconButton(
        onClick = onBackClicked,
        modifier = Modifier.background(White.copy(alpha = 0.7f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = SoftBlack
        )
    }
}

@Composable
fun CompleteButton(onCompleteClicked: () -> Unit) {
    Button(
        onClick = onCompleteClicked,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = SoftBlack),
    ) {
        Text(
            text = "Complete",
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun WebView() {
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
            webView.loadUrl("https://its-christmas-1f0ea.firebaseapp.com/")
        }
    )
}

@Composable
fun BoxScope.ShareButton(onShareClicked: () -> Unit) {
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
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "Share",
                    tint = SoftBlack
                )
                Text(
                    text = "Share",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CardEditorScreenPreview() {
    CardShareScreen()
}
