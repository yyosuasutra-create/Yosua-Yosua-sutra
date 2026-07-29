package com.example.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlowingCyan
import com.example.ui.theme.LaserPink
import com.example.ui.theme.MvpGold
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceCardBg
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlin.random.Random

data class SnakePoint(val x: Int, val y: Int)
enum class Direction { UP, DOWN, LEFT, RIGHT }

@Composable
fun SnakeGame(
    onBack: () -> Unit,
    onSaveScore: (Int) -> Unit
) {
    val gridSize = 16
    val snake = remember { mutableStateListOf(SnakePoint(8, 8), SnakePoint(8, 9), SnakePoint(8, 10)) }
    var food by remember { mutableStateOf(SnakePoint(5, 5)) }
    var direction by remember { mutableStateOf(Direction.UP) }
    var score by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(180) // Game speed

                val head = snake.first()
                val newHead = when (direction) {
                    Direction.UP -> SnakePoint(head.x, head.y - 1)
                    Direction.DOWN -> SnakePoint(head.x, head.y + 1)
                    Direction.LEFT -> SnakePoint(head.x - 1, head.y)
                    Direction.RIGHT -> SnakePoint(head.x + 1, head.y)
                }

                // Check collision with walls or self
                if (newHead.x < 0 || newHead.x >= gridSize || newHead.y < 0 || newHead.y >= gridSize || snake.contains(newHead)) {
                    isGameOver = true
                    onSaveScore(score)
                } else {
                    snake.add(0, newHead)
                    if (newHead == food) {
                        score += 10
                        food = SnakePoint(Random.nextInt(gridSize), Random.nextInt(gridSize))
                    } else {
                        snake.removeAt(snake.size - 1)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_snake")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Text("ULAR GALAKSI RETRO", color = MvpGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Skor: $score", color = GlowingCyan, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceCardBg)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / gridSize

                // Draw Food
                drawRect(
                    color = LaserPink,
                    topLeft = Offset(food.x * cellSize, food.y * cellSize),
                    size = Size(cellSize - 2, cellSize - 2)
                )

                // Draw Snake
                snake.forEachIndexed { index, point ->
                    drawRect(
                        color = if (index == 0) GlowingCyan else NeonViolet,
                        topLeft = Offset(point.x * cellSize, point.y * cellSize),
                        size = Size(cellSize - 2, cellSize - 2)
                    )
                }
            }

            if (isGameOver) {
                Card(
                    modifier = Modifier.align(Alignment.Center),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceBlack)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("GAME OVER!", color = LaserPink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Skor Akhir: $score", color = TextPrimary, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                snake.clear()
                                snake.addAll(listOf(SnakePoint(8, 8), SnakePoint(8, 9), SnakePoint(8, 10)))
                                direction = Direction.UP
                                score = 0
                                isGameOver = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Main Lagi")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // D-Pad Controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { if (direction != Direction.DOWN) direction = Direction.UP },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
            ) {
                Text("▲ UP")
            }

            Row {
                Button(
                    onClick = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
                ) {
                    Text("◄ LEFT")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = { if (direction != Direction.LEFT) direction = Direction.RIGHT },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
                ) {
                    Text("RIGHT ►")
                }
            }

            Button(
                onClick = { if (direction != Direction.UP) direction = Direction.DOWN },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
            ) {
                Text("▼ DOWN")
            }
        }
    }
}
