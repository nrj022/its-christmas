package com.itschristmas.card.cardeditor.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itschristmas.card.cardeditor.model.BaseTabItems
import com.itschristmas.designsystem.theme.Gray
import com.itschristmas.designsystem.theme.SoftBlack
import com.itschristmas.designsystem.theme.White

@Composable
fun BaseTabs(tabs: List<BaseTabItems>, selectedTabId: String, onTabSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        tabs.forEach { item ->
            val isSelected = selectedTabId == item.id
            val containerColor = if (isSelected) SoftBlack else Gray
            val contentColor = if (isSelected) White else SoftBlack

            Button(
                onClick = { onTabSelected(item.id) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
            ) {
                Text(
                    text = stringResource(item.textRes),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}