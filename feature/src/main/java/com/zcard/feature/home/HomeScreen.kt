package com.zcard.feature.home

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.zcard.feature.R
import com.zcard.designsystem.theme.DimGray
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.designsystem.util.DrawableResProvider.getBgThumbByKey
import com.zcard.feature.home.model.CardItem
import com.zcard.feature.home.util.formatRelativeTime

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel(), onCardClick: (Long) -> Unit = {}) {
    val cardItems by viewModel.cardList.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.loadCards()
        onPauseOrDispose {  }
    }

    MainContent(
        cardItems = cardItems,
        onCardClick = onCardClick
    )
}

@Composable
fun MainContent(modifier: Modifier = Modifier, cardItems: List<CardItem> = emptyList(), onCardClick: (Long) -> Unit = {}) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .systemBarsPadding()
            .padding(horizontal = 18.dp),
        topBar = { TopBar {} },
    ) {
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize().padding(it),
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PreviewGifImage { onCardClick(-1) }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(R.string.main_title_card_dashboard),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (cardItems.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyCardSection()
                }
            } else {
                items(
                    items = cardItems,
                ) { item ->
                    CardItem(
                        cardTitle = item.card.title,
                        updatedAt = formatRelativeTime(item.card.updatedAt),
                        thumbnailKey = item.thumbnailKey,
                        onClick = { onCardClick(item.card.cardId) }
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TopBar(onSettingClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier.height(40.dp).aspectRatio(2.3f),
            painter = painterResource(R.drawable.logo_full),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        IconButton(onClick = onSettingClick) {
            Icon(painterResource(R.drawable.ic_setting), contentDescription = null)
        }
    }
}

@Composable
fun PreviewGifImage(onCardClick: () -> Unit) {
    val gifEnabledLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if ( SDK_INT >= 28 ) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Gray),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            imageLoader = gifEnabledLoader,
            model = R.drawable.gif_happy_birthday,
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(R.string.main_cd_async_image)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftBlack.copy(alpha = 0.15f))
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                Column {
                    Text(
                        color = White,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = 0.4.sp,
                        fontSize = 12.sp,
                        text = stringResource(R.string.main_title_design_card_sub_prompt)
                    )
                    Text(
                        color = White,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.4.sp,
                        fontSize = 20.sp,
                        text = stringResource(R.string.main_title_design_card_prompt)
                    )
                }
                CreateCardButton(onClick = onCardClick)
            }
        }
    }
}

@Composable
fun CreateCardButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = White,
            contentColor = SoftBlack,
            disabledContainerColor = Gray,
            disabledContentColor = White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.main_button_create),
                style = MaterialTheme.typography.labelSmall
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.main_cd_arrow_forward_icon),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun CardItem(cardTitle: String, updatedAt: String, thumbnailKey: String?, onClick: () -> Unit) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(25.dp))
                .clickable { onClick() },
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(getBgThumbByKey(thumbnailKey)),
                    contentDescription = stringResource(R.string.main_cd_card),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier.padding(horizontal = 6.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = cardTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = updatedAt,
                color = DimGray,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
fun EmptyCardSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_card_star),
            tint = Gray,
            contentDescription = stringResource(R.string.main_cd_empty_card_icon)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.main_text_notice_empty_card_1),
            style = MaterialTheme.typography.labelSmall,
            color = Gray
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.main_text_notice_empty_card_2),
            style = MaterialTheme.typography.labelSmall,
            color = Gray
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    ZCardTheme {
        MainContent()
    }
}