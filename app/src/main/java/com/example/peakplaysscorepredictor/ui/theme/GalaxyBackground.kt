package com.example.peakplaysscorepredictor.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.random.Random

@Composable
fun GalaxyBackground(
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF1a237e), // Deep blue
        Color(0xFF000051), // Darker blue
        Color(0xFF4a148c), // Deep purple
        Color(0xFF000000)  // Black
    )

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = colors,
                    center = Offset.Zero,
                    radius = 1000f
                )
            )
    ) {
        // Animated stars
        val stars = remember {
            List(100) {
                Star(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    size = Random.nextFloat() * 2f + 1f,
                    alpha = Random.nextFloat() * 0.5f + 0.5f
                )
            }
        }

        val starAlpha = infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "starAlpha"
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotation.value
                }
        ) {
            stars.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = star.alpha * starAlpha.value),
                    radius = star.size,
                    center = Offset(
                        x = size.width * star.x,
                        y = size.height * star.y
                    )
                )
            }
        }
    }
}

private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val alpha: Float
) 