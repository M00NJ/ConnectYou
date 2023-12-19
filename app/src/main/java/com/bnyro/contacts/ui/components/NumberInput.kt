package com.bnyro.contacts.ui.components

import android.annotation.SuppressLint
import android.telephony.SubscriptionInfo
import android.view.SoundEffectConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bnyro.contacts.R

val keypadNumbers = arrayOf(
    arrayOf("1", "2", "3"),
    arrayOf("4", "5", "6"),
    arrayOf("7", "8", "9"),
    arrayOf("*", "0", "#")
)

@SuppressLint("NewApi")
@Composable
fun NumberInput(
    onNumberInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDial: () -> Unit,
    subscriptions: List<SubscriptionInfo>?,
    onSubscriptionIndexChange: (Int) -> Unit
) {
    val buttonSpacing = 8.dp
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        keypadNumbers.forEach { col ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                col.forEach {
                    NumpadButton(
                        text = it,
                        onClick = {
                            onNumberInput(it)
                        }
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            NumpadButton(
                onClick = { onDial.invoke() },
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    Icons.Rounded.Call,
                    contentDescription = stringResource(R.string.dial),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            NumpadButton(
                onClick = { onDelete.invoke() },
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(
                    Icons.Rounded.Backspace,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        if (subscriptions != null && subscriptions.size >= 2) {
            var currentSub by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) {
                currentSub = 0
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(onClick = {
                    val newSub = (currentSub + 1) % subscriptions.size
                    currentSub = newSub
                    onSubscriptionIndexChange(newSub)
                }) {
                    Text(
                        text = "SIM ${subscriptions[currentSub].simSlotIndex + 1} - ${subscriptions[currentSub].displayName}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun RowScope.NumpadButton(
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
    aspectRatio: Float = 1f,
    onClick: () -> Unit,
    onLongClick: () -> Unit = { }
) {
    NumpadButton(
        backgroundColor = backgroundColor,
        aspectRatio = aspectRatio,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.NumpadButton(
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    aspectRatio: Float = 1f,
    onClick: () -> Unit,
    onLongClick: () -> Unit = { },
    content: @Composable (BoxScope.() -> Unit)
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .combinedClickable(
                onClick = {
                    onClick.invoke()
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                },
                onLongClick = {
                    onLongClick.invoke()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            .background(backgroundColor)
            .aspectRatio(2f)
            .weight(aspectRatio),
        content = content
    )
}

@Composable
fun ColumnScope.PhoneNumberDisplay(displayText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1.0f)
    ) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Bottom)
        ) {
            // TODO: Contact Suggestions
        }
        val scroll = rememberScrollState()
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = displayText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
