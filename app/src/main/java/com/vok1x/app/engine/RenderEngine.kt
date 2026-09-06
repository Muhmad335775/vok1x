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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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
            phase += 0.05f
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = size.minDimension / 5f

        val hue = ((colorSeed * 360f) + mode.colorTendency) % 360f
        val skinColor = Color.hsv(hue, 0.35f, 1f)
        val accentColor = Color.hsv((hue + 40f) % 360f, 0.8f, 0.9f)

        val loudness = volume * mode.volumeReactivity
        val jitter = speed * mode.speedReactivity
        val bounce = pitch * mode.pitchReactivity

        // Face jitter offset (used by Chaotic Shaker, Terrified Trembler).
        val jitterX = if (mode.id == 0 || mode.id == 4)
            (Random.nextFloat() - 0.5f) * jitter * 20f else 0f
        val jitterY = if (mode.id == 0 || mode.id == 4)
            (Random.nextFloat() - 0.5f) * jitter * 20f else 0f

        // Dancing Fool bounces up/down with the beat.
        val danceOffsetY = if (mode.id == 7) sin(phase * 4f) * bounce * 30f else 0f

        val cx = centerX + jitterX
        val cy = centerY + jitterY + danceOffsetY

        val faceRadius = baseRadius * (0.9f + loudness * 0.4f)

        // Base face circle — every mode has this.
        drawCircle(color = skinColor, radius = faceRadius, center = Offset(cx, cy))

        when (mode.id) {
            0 -> {
                // Chaotic Shaker: erratic zig-zag outline around the face.
                for (i in 0 until 8) {
                    val angle = (i / 8f) * 2f * Math.PI.toFloat()
                    val spikeLen = faceRadius * (0.2f + loudness * 0.3f)
                    val x1 = cx + faceRadius * cos(angle)
                    val y1 = cy + faceRadius * sin(angle)
                    val x2 = cx + (faceRadius + spikeLen) * cos(angle + phase)
                    val y2 = cy + (faceRadius + spikeLen) * sin(angle + phase)
                    drawLine(accentColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 6f)
                }
            }
            1 -> {
                // Time-Traveling Elder: pointed hat + a wand line.
                val hatHeight = faceRadius * 1.2f
                drawLine(
                    accentColor,
                    Offset(cx, cy - faceRadius),
                    Offset(cx, cy - faceRadius - hatHeight),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
                drawCircle(Color.White, faceRadius * 0.15f, Offset(cx, cy - faceRadius - hatHeight))
                // Altitude line rises with pitch.
                drawLine(
                    Color.White.copy(alpha = 0.5f),
                    Offset(cx - faceRadius, cy + faceRadius * 0.5f - bounce * 40f),
                    Offset(cx + faceRadius, cy + faceRadius * 0.5f - bounce * 40f),
                    strokeWidth = 3f
                )
            }
            2 -> {
                // Exaggerated Crier: teardrops that grow with volume spikes.
                val tearSize = faceRadius * (0.15f + loudness * 0.5f)
                drawOval(
                    Color(0xFF4FC3F7),
                    topLeft = Offset(cx - faceRadius * 0.45f, cy + faceRadius * 0.1f),
                    size = Size(tearSize * 0.6f, tearSize)
                )
                drawOval(
                    Color(0xFF4FC3F7),
                    topLeft = Offset(cx + faceRadius * 0.15f, cy + faceRadius * 0.1f),
                    size = Size(tearSize * 0.6f, tearSize)
                )
            }
            3 -> {
                // Smug Mocker: one raised, slanted eyebrow.
                drawLine(
                    Color.Black,
                    Offset(cx - faceRadius * 0.4f, cy - faceRadius * 0.3f),
                    Offset(cx - faceRadius * 0.1f, cy - faceRadius * 0.5f - bounce * 15f),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    Color.Black,
                    Offset(cx + faceRadius * 0.1f, cy - faceRadius * 0.35f),
                    Offset(cx + faceRadius * 0.4f, cy - faceRadius * 0.35f),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }
            4 -> {
                // Terrified Trembler: wide eyes that grow on sharp sounds.
                val eyeSize = faceRadius * (0.15f + loudness * 0.25f)
                drawCircle(Color.White, eyeSize, Offset(cx - faceRadius * 0.35f, cy - faceRadius * 0.1f))
                drawCircle(Color.White, eyeSize, Offset(cx + faceRadius * 0.35f, cy - faceRadius * 0.1f))
                drawCircle(Color.Black, eyeSize * 0.4f, Offset(cx - faceRadius * 0.35f, cy - faceRadius * 0.1f))
                drawCircle(Color.Black, eyeSize * 0.4f, Offset(cx + faceRadius * 0.35f, cy - faceRadius * 0.1f))
            }
            5 -> {
                // Fast Talker: mouth flaps open/close faster than speech.
                val mouthOpen = ((sin(phase * (6f + jitter * 6f)) + 1f) / 2f) * faceRadius * 0.5f
                drawOval(
                    Color.Black,
                    topLeft = Offset(cx - faceRadius * 0.3f, cy + faceRadius * 0.2f),
                    size = Size(faceRadius * 0.6f, mouthOpen + 8f)
                )
            }
            6 -> {
                // Exploding Angry: reddens and puffs with volume.
                val puff = faceRadius * (1f + loudness * 0.6f)
                drawCircle(
                    color = Color(0xFFFF3B30).copy(alpha = 0.5f),
                    radius = puff,
                    center = Offset(cx, cy)
                )
                for (i in 0 until 12) {
                    val angle = (i / 12f) * 2f * Math.PI.toFloat()
                    val x = cx + puff * cos(angle)
                    val y = cy + puff * sin(angle)
                    drawLine(Color(0xFFFF3B30), Offset(cx, cy), Offset(x, y), strokeWidth = 4f)
                }
            }
            7 -> {
                // Dancing Fool: already bounces via danceOffsetY; add rhythm arcs.
                drawArc(
                    accentColor,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - faceRadius, cy + faceRadius * 0.2f),
                    size = Size(faceRadius * 2f, faceRadius),
                    style = Stroke(width = 6f)
                )
            }
            8 -> {
                // Dramatic Sulker: slow drooping mouth, theatrical sigh.
                val droop = faceRadius * (0.2f + bounce * 0.2f)
                drawArc(
                    Color.Black,
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - faceRadius * 0.4f, cy + droop),
                    size = Size(faceRadius * 0.8f, faceRadius * 0.4f),
                    style = Stroke(width = 5f)
                )
            }
        }
    }
}
