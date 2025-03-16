package com.example.thinkr.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable that renders an individual chat message bubble.
 *
 * Displays a message with different styles and alignments based on whether the current
 * user is the sender. Sender messages appear with primary color background aligned to the right,
 * while received messages use secondary container color aligned to the left.
 * Each message includes the message content and a timestamp.
 *
 * @param message The message object to display, containing content, timestamp and sender information.
 */
@Composable
fun ChatMessageItem(message: Message) {
    val isSender = message.isSender

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isSender) Alignment.End else Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isSender) 16.dp else 4.dp,
                        bottomEnd = if (isSender) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isSender) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content.value,
                style = TextStyle(
                    color = if (isSender) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 16.sp
                )
            )

            // Timestamp
            Text(
                text = message.formattedTime,
                style = TextStyle(
                    color = if (isSender) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
