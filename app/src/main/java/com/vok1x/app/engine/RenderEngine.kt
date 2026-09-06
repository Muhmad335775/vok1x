package com.vok1x.app.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RenderEngine(
    volume: Float,
    pitch: Float,
    speed: Float,
    mode: ModeConfig,
    colorSeed: Float,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            phase += 0.02f
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val hue = ((colorSeed * 360f) + (pitch * mode.pitchReactivity * 120f) + mode.colorTendency) % 360f
        val color1 = Color.hsv(hue, 0.8f, 1f)
        val color2 = Color.hsv((hue + 60f) % 360f, 0.7f, 1f)
        val color3 = Color.hsv((hue + 300f) % 360f, 0.7f, 1f)

        val baseRadius = size.minDimension / 4f
        val radius = baseRadius * (0.6f + volume * mode.volumeReactivity * 0.8f)
        val wobble = 1f + speed * mode.speedReactivity * 0.5f

        val points = 32
        val path = Path()
        for (i in 0..points) {
            val angle = (i.toFloat() / points) * 2f * Math.PI.toFloat()
            val r = radius * (1f + 0.15f * sin(angle * (3 + mode.id % 4) + phase * wobble))
            val x = centerX + r * cos(angle)
            val y = centerY + r * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        drawPath(
            path = path,
            brush = Brush.sweepGradient(listOf(color1, color2, color3, color1)),
            style = Stroke(width = 10f)
        )
    }
}
