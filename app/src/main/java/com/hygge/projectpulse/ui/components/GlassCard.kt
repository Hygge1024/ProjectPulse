package com.hygge.projectpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

private val GlassGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFEEF1F5),
        Color(0xFFF7F8FA),
        Color(0xFFE3E7EC)
    )
)

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    blurRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val hazeState = remember { HazeState() }
    val surface = MaterialTheme.colorScheme.surface
    val glassStyle = HazeDefaults.style(
        tint = surface.copy(alpha = 0.55f),
        blurRadius = blurRadius,
        noiseFactor = 0.1f
    )
    val borderColor = if (surface.luminance() < 0.5f) {
        Color.White.copy(alpha = 0.6f)
    } else {
        Color.Black.copy(alpha = 0.12f)
    }

    Box(modifier = modifier.clip(shape)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GlassGradient)
                .haze(
                    state = hazeState,
                    style = HazeDefaults.style(
                        tint = surface.copy(alpha = 0.2f),
                        blurRadius = blurRadius,
                        noiseFactor = 0.1f
                    )
                )
        )
        Card(
            modifier = Modifier
                .fillMaxSize()
                .hazeChild(state = hazeState, shape = shape, style = glassStyle)
                .border(1.5.dp, borderColor, shape),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    blurRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val hazeState = remember { HazeState() }
    val surface = MaterialTheme.colorScheme.surface
    val glassStyle = HazeDefaults.style(
        tint = surface.copy(alpha = 0.55f),
        blurRadius = blurRadius,
        noiseFactor = 0.1f
    )
    val borderColor = if (surface.luminance() < 0.5f) {
        Color.White.copy(alpha = 0.6f)
    } else {
        Color.Black.copy(alpha = 0.12f)
    }

    Box(modifier = modifier.clip(shape)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GlassGradient)
                .haze(
                    state = hazeState,
                    style = HazeDefaults.style(
                        tint = surface.copy(alpha = 0.2f),
                        blurRadius = blurRadius,
                        noiseFactor = 0.1f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeChild(state = hazeState, shape = shape, style = glassStyle)
                .border(1.5.dp, borderColor, shape),
            content = content
        )
    }
}
