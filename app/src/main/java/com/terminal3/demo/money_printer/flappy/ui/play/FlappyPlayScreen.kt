package com.terminal3.demo.money_printer.flappy.ui.play

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.PathParser
import com.terminal3.demo.money_printer.R
import com.terminal3.demo.money_printer.flappy.FlappyUiState
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun FlappyPlayScreen(
    uiState: FlappyUiState,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    // Load simple bird SVG path
    val birdPath = remember {
        val svg = context.resources.openRawResource(R.raw.flappy_bird).bufferedReader().use { it.readText() }
        val data = svg.substringAfter("d=\"").substringBefore("\"")
        PathParser().parsePathString(data).toPath()
    }

    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }
    var birdY by remember { mutableStateOf(0f) }
    var velocity by remember { mutableStateOf(0f) }
    var pipeX by remember { mutableStateOf(0f) }
    var gapY by remember { mutableStateOf(0f) }

    LaunchedEffect(canvasWidth, canvasHeight) {
        if (canvasWidth == 0f) return@LaunchedEffect
        birdY = canvasHeight / 2f
        pipeX = canvasWidth
        gapY = canvasHeight / 2f
        var lastTime = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            val delta = (now - lastTime) / 1_000_000_000f
            lastTime = now
            velocity += 400f * delta
            birdY += velocity * delta
            pipeX -= 200f * delta
            if (pipeX < -100f) {
                pipeX = canvasWidth
                gapY = Random.nextFloat() * (canvasHeight - 300f) + 150f
            }
            if (birdY < 0 || birdY > canvasHeight ||
                (pipeX < canvasWidth / 4f + 24f && pipeX + 100f > canvasWidth / 4f &&
                        (birdY < gapY - 150f || birdY + 24f > gapY + 150f))) {
                birdY = canvasHeight / 2f
                velocity = 0f
                pipeX = canvasWidth
            }
            kotlinx.coroutines.delay(16L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (uiState.selectedTheme == "night") Color(0xFF001848) else Color(0xFF87CEEB))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { velocity = -300f } }
                .onSizeChanged {
                    canvasWidth = it.width.toFloat()
                    canvasHeight = it.height.toFloat()
                }
        ) {
            val pipeWidth = 100f
            val gapHeight = 300f
            // Draw pipes
            drawRect(
                color = Color.Green,
                topLeft = Offset(pipeX, 0f),
                size = Size(pipeWidth, gapY - gapHeight / 2)
            )
            drawRect(
                color = Color.Green,
                topLeft = Offset(pipeX, gapY + gapHeight / 2),
                size = Size(pipeWidth, canvasHeight - (gapY + gapHeight / 2))
            )
            // Draw bird from SVG path
            val birdColor = if (uiState.selectedBird == "red") Color.Red else Color.Yellow
            withTransform({
                translate(left = canvasWidth / 4f - 12f, top = birdY - 12f)
            }) {
                drawPath(path = birdPath as Path, color = birdColor)
            }
        }
        Button(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("Back")
        }
    }
}
