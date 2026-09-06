package com.vok1x.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vok1xTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VokScreen()
                }
            }
        }
    }
}

@Composable
fun VokScreen() {
    val context = LocalContext.current

    var micGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableFloatStateOf(0f) }
    var secondsLeft by remember { mutableIntStateOf(25) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        micGranted = granted
        permissionDenied = !granted
    }

    LaunchedEffect(Unit) {
        if (!micGranted) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // 25-second countdown while recording; auto-stops at zero.
    LaunchedEffect(isRecording) {
        if (isRecording) {
            secondsLeft = 25
            while (secondsLeft > 0 && isRecording) {
                delay(1000)
                secondsLeft -= 1
            }
            if (secondsLeft <= 0) {
                isRecording = false
            }
        }
    }

    // Microphone capture loop.
    DisposableEffect(isRecording) {
        var audioRecord: AudioRecord? = null
        var thread: Thread? = null
        if (isRecording && micGranted) {
            val sampleRate = 44100
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(2048)

            @SuppressLint("MissingPermission")
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            audioRecord = recorder
            recorder.startRecording()

            thread = Thread {
                val buffer = ShortArray(bufferSize)
                while (isRecording) {
                    val read = recorder.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        var sum = 0.0
                        for (i in 0 until read) sum += (buffer[i] * buffer[i]).toDouble()
                        val rms = sqrt(sum / read).toFloat()
                        volumeLevel = (rms / Short.MAX_VALUE).coerceIn(0f, 1f)
                    }
                }
            }
            thread.start()
        }
        onDispose {
            thread?.interrupt()
            audioRecord?.stop()
            audioRecord?.release()
            volumeLevel = 0f
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1B0F3A),
            Color(0xFF3A1F6B),
            Color(0xFF6C2E9C)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar: app name (always English) + share button, share sits slightly lower.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Vok1x",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out Vok1x! https://github.com/${context.packageName}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, null))
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share app",
                        tint = Color.White
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {

                Box(
                    modifier = Modifier.fillMaxWidth().weight(3f),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        permissionDenied -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Vok1x needs microphone access to work.",
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", context.packageName, null)
                                        )
                                    )
                                }) { Text(text = "Open Settings") }
                            }
                        }
                        isRecording -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Canvas(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                ) {
                                    val centerX = size.width / 2f
                                    val centerY = size.height / 2f
                                    val baseRadius = size.minDimension / 4f
                                    val radius = baseRadius * (0.6f + volumeLevel * 1.2f)
                                    val points = 32
                                    val path = androidx.compose.ui.graphics.Path()
                                    for (i in 0..points) {
                                        val angle = (i.toFloat() / points) * 2f * Math.PI.toFloat()
                                        val x = centerX + radius * cos(angle)
                                        val y = centerY + radius * sin(angle)
                                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                    }
                                    path.close()
                                    drawPath(
                                        path = path,
                                        brush = Brush.sweepGradient(
                                            listOf(
                                                Color(0xFFFFD86B),
                                                Color(0xFFFF6BAE),
                                                Color(0xFF6C4CE0),
                                                Color(0xFFFFD86B)
                                            )
                                        ),
                                        style = Stroke(width = 10f)
                                    )
                                }
                                Text(
                                    text = "$secondsLeft s",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            Text(
                                text = "Tap the mic to start",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "128,402 voices sent today",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )
                        }

                        IconButton(
                            onClick = { isRecording = !isRecording },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = if (isRecording)
                                            listOf(Color(0xFFFF4D6D), Color(0xFFC9184A))
                                        else
                                            listOf(Color(0xFF9D7BEA), Color(0xFF6C4CE0))
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Mic",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Vok1xTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
