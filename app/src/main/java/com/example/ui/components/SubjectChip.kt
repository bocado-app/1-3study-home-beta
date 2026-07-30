package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.getSubjectBgColor
import com.example.ui.theme.getSubjectColor
import com.example.ui.theme.getSubjectIcon

@Composable
fun SubjectChip(
    subject: String,
    isSelected: Boolean,
    count: Int,
    onClick: () -> Unit
) {
    val baseColor = if (subject == "전체") com.example.ui.theme.EditorialPurple else getSubjectColor(subject)
    val baseBgColor = if (subject == "전체") com.example.ui.theme.EditorialLightPurple else getSubjectBgColor(subject)
    val icon = if (subject == "전체") "✨" else getSubjectIcon(subject)

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) baseColor else baseBgColor,
        label = "chipBgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else baseColor,
        label = "chipTextColor"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) baseColor else baseColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = icon,
                fontSize = 14.sp
            )
            Text(
                text = subject,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            )
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else baseColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )
                }
            }
        }
    }
}
