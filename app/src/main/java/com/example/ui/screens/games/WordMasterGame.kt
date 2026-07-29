package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.TextSecondary

@Composable
fun WordMasterGame(
    onBack: () -> Unit,
    onSaveScore: (Int) -> Unit
) {
    val secretWords = remember { listOf("ORBIT", "SMART", "CYBER", "LASER", "ROBOT", "CLOUD") }
    var targetWord by remember { mutableStateOf(secretWords.random()) }
    val guesses = remember { mutableStateListOf<String>() }
    var currentInput by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var isWon by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_wordmaster")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Text("TEBAK KATA GALAKSI", color = MvpGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Skor: $score", color = GlowingCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tebak 5 Huruf Istilah Antariksa/Sains (Tersisa ${6 - guesses.size} Kesempatan)",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid 6 Rows x 5 Letters
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0..5) {
                val rowWord = if (row < guesses.size) guesses[row] else if (row == guesses.size) currentInput else ""
                val isEvaluated = row < guesses.size

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (col in 0..4) {
                        val char = if (col < rowWord.length) rowWord[col] else ' '

                        val cellBg = if (isEvaluated) {
                            if (char == targetWord[col]) Color(0xFF1B4332) // Green
                            else if (targetWord.contains(char)) Color(0xFF854D0E) // Yellow
                            else Color(0xFF2A2D48) // Gray
                        } else {
                            SpaceCardBg
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cellBg)
                                .border(1.dp, NeonViolet, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char.toString(),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isWon && !isGameOver) {
            // Virtual Keyboard
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val row1 = "QWERTYUIOP"
                val row2 = "ASDFGHJKL"
                val row3 = "ZXCVBNM"

                listOf(row1, row2, row3).forEach { keys ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        keys.forEach { char ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SpaceCardBg)
                                    .clickable {
                                        if (currentInput.length < 5) {
                                            currentInput += char
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 10.dp)
                            ) {
                                Text(char.toString(), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            if (currentInput.isNotEmpty()) {
                                currentInput = currentInput.dropLast(1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
                    ) {
                        Icon(imageVector = Icons.Default.Backspace, contentDescription = "Hapus")
                    }

                    Button(
                        onClick = {
                            if (currentInput.length == 5) {
                                guesses.add(currentInput)
                                if (currentInput == targetWord) {
                                    isWon = true
                                    score += 50
                                    onSaveScore(score)
                                } else if (guesses.size >= 6) {
                                    isGameOver = true
                                }
                                currentInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                    ) {
                        Text("SUBMIT KATA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isWon) "SELAMAT! KATA BENAR: $targetWord" else "GAME OVER! KATA BENAR: $targetWord",
                color = if (isWon) GlowingCyan else LaserPink,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    guesses.clear()
                    currentInput = ""
                    targetWord = secretWords.random()
                    isWon = false
                    isGameOver = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kata Baru")
            }
        }
    }
}
