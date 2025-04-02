package com.example.thinkr.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A composable function that displays a button with a gradient background.
 *
 * @param text The text to display on the button.
 * @param gradientColors The list of colors to use for the gradient background.
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param icon An optional icon to display alongside the text.
 */
@Composable
fun GradientButton(
    text: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(
                brush = Brush.horizontalGradient(colors = gradientColors)
            )
            .padding(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = if (icon != null) 8.dp else 0.dp)
            )

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
