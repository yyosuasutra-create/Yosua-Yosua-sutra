package com.example.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Bullet(var x: Float, var y: Float)
data class Asteroid(var x: Float, var y: Float, val speed: Float, val radius: Float)

@Composable
fun SpaceShooterGame(
    onBack: () -> Unit,
    onSaveScore: (Int) -> Unit
) {
    var shipX by remember { mutableStateOf(0.5f) } // Normalized 0f to 1f
    var score by remember { mutableStateOf(0) }
    var health by remember { mutableStateOf(100) }
    var isGameOver by remember { mutableStateOf(false) }

    val bullets = remember { mutableStateListOf<Bullet>() }
    val asteroids = remember { mutableStateListOf<Asteroid>() }

    // Game loop
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(30) // ~33 FPS

                // Move bullets
                bullets.forEach { it.y -= 0.03f }
                bullets.removeAll { it.y < 0f }

                // Move asteroids
                asteroids.forEach { it.y += it.speed }

                // Spawn asteroids
                if (Random.nextFloat() < 0.12f) {
                    asteroids.add(
                        Asteroid(
                            x = Random.nextFloat(),
                            y = -0.05f,
                            speed = Random.nextFloat() * 0.015f + 0.01f,
                            radius = Random.nextFloat() * 0.03f + 0.03f
                        )
                    )
                }

                // Check bullet collisions
                val bulletsToRemove = mutableListOf<Bullet>()
                val asteroidsToRemove = mutableListOf<Asteroid>()

                bullets.forEach { bullet ->
                    asteroids.forEach { asteroid ->
                        val dx = bullet.x - asteroid.x
                        val dy = bullet.y - asteroid.y
                        if (dx * dx + dy * dy < asteroid.radius * asteroid.radius) {
                            bulletsToRemove.add(bullet)
                            asteroidsToRemove.add(asteroid)
                            score += 10
                        }
                    }
                }

                bullets.removeAll(bulletsToRemove)
                asteroids.removeAll(asteroidsToRemove)

                // Check ship collisions
                asteroids.forEach { asteroid ->
                    if (asteroid.y > 0.85f && Math.abs(asteroid.x - shipX) < 0.08f) {
                        asteroidsToRemove.add(asteroid)
                        health -= 20
                        if (health <= 0) {
                            isGameOver = true
                            onSaveScore(score)
                        }
                    }
                }
                asteroids.removeAll(asteroidsToRemove)
                asteroids.removeAll { it.y > 1.05f }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(12.dp)
    ) {
        // Game Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_space_shooter")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
            }

            Text(
                text = "PERANG ANTARIKSA",
                color = MvpGold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = "Skor: $score",
                color = GlowingCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }

        // Health Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Shield: ", color = TextSecondary, fontSize = 11.sp)
            LinearProgressIndicator(
                progress = { (health / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (health > 40) GlowingCyan else LaserPink,
                trackColor = SpaceCardBg
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Game Canvas Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceCardBg)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newX = shipX + (dragAmount.x / size.width)
                        shipX = newX.coerceIn(0.05f, 0.95f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Draw Stars background
                for (i in 0..30) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 2f,
                        center = Offset(
                            x = (i * 37) % canvasW,
                            y = (i * 73) % canvasH
                        )
                    )
                }

                // Draw Bullets
                bullets.forEach { b ->
                    drawCircle(
                        color = LaserPink,
                        radius = 8f,
                        center = Offset(b.x * canvasW, b.y * canvasH)
                    )
                }

                // Draw Asteroids
                asteroids.forEach { a ->
                    drawCircle(
                        color = Color(0xFF8D99AE),
                        radius = a.radius * canvasW,
                        center = Offset(a.x * canvasW, a.y * canvasH)
                    )
                }

                // Draw Player Spaceship
                val shipPxX = shipX * canvasW
                val shipPxY = 0.88f * canvasH

                val path = Path().apply {
                    moveTo(shipPxX, shipPxY - 25f)
                    lineTo(shipPxX - 20f, shipPxY + 20f)
                    lineTo(shipPxX, shipPxY + 10f)
                    lineTo(shipPxX + 20f, shipPxY + 20f)
                    close()
                }
                drawPath(path = path, color = GlowingCyan)
            }

            if (isGameOver) {
                Card(
                    modifier = Modifier.align(Alignment.Center),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceBlack)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("GAME OVER!", color = LaserPink, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Skor Akhir: $score", color = TextPrimary, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                score = 0
                                health = 100
                                bullets.clear()
                                asteroids.clear()
                                isGameOver = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Main Lagi")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Shoot Button & Left/Right Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { shipX = (shipX - 0.08f).coerceAtLeast(0.05f) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
            ) {
                Text("◄ Kiri", color = TextPrimary)
            }

            Button(
                onClick = {
                    if (!isGameOver) {
                        bullets.add(Bullet(x = shipX, y = 0.86f))
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LaserPink),
                modifier = Modifier.testTag("fire_laser_button")
            ) {
                Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Tembak")
                Spacer(modifier = Modifier.width(4.dp))
                Text("TEMBAK LASER", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { shipX = (shipX + 0.08f).coerceAtMost(0.95f) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
            ) {
                Text("Kanan ►", color = TextPrimary)
            }
        }
    }
}
