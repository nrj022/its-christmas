package com.itschristmas.main.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itschristmas.main.R

val halloweenImages = listOf(
    R.drawable.img_sample,
    R.drawable.img_sample,
    R.drawable.img_sample,
    R.drawable.img_sample,
    R.drawable.img_sample,
    R.drawable.img_sample,
)
@Composable
fun MainScreen() {
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            NewCardFab(onClick = { /* TODO: Handle new card creation */ })
        }
    ) { paddingValues ->
        CardContent(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
            cardImages = halloweenImages
        )
    }
}

@Composable
fun CardContent(modifier: Modifier = Modifier, cardImages: List<Int>) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenTitle(text = "My Cards")
        CardGrid(cardImages = cardImages)
    }
}

@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 24.dp)
    )
}

@Composable
fun CardGrid(cardImages: List<Int>, modifier: Modifier = Modifier) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(cardImages) { imageRes ->
            CardItem(imageRes = imageRes)
        }
    }
}

@Composable
fun CardItem(imageRes: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Halloween card image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun NewCardFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(40.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Create New Card"
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "New Card",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}