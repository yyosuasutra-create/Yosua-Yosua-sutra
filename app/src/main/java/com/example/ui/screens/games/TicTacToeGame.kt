package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import kotlin.random.Random

@Composable
fun TicTacToeGame(
    onBack: () -> Unit,
    onSaveScore: (Int) -> Unit
) {
    val board = remember { mutableStateListOf("", "", "", "", "", "", "", "", "") }
    var isUserTurn by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf<String?>(null) } // "X" (User), "O" (Galaksi AI), "DRAW", or null
    var score by remember { mutableStateOf(0) }

    fun checkWinner(): String? {
        val winningCombos = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (combo in winningCombos) {
            val (a, b, c) = combo
            if (board[a].isNotEmpty() && board[a] == board[b] && board[a] == board[c]) {
                return board[a]
            }
        }
        if (board.none { it.isEmpty() }) return "DRAW"
        return null
    }

    fun makeAiMove() {
        val emptyIndices = board.indices.filter { board[it].isEmpty() }
        if (emptyIndices.isNotEmpty() && winner == null) {
            val aiChoice = emptyIndices.random()
            board[aiChoice] = "O"
            winner = checkWinner()
            if (winner == "O") {
                // AI won
            }
            isUserTurn = true
        }
    }

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_tictactoe")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Text("SILANG LINGKARAN AI", color = MvpGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Skor: $score", color = GlowingCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (winner != null) {
                when (winner) {
                    "X" -> "MENANG! +30 SKOR"
                    "O" -> "GALAKSI AI MENANG"
                    else -> "SERI!"
                }
            } else if (isUserTurn) "Giliran Anda (X)" else "Galaksi AI Berpikir (O)...",
            color = if (winner == "X") MvpGold else if (winner == "O") LaserPink else GlowingCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3x3 Board
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SpaceCardBg)
                                .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable(enabled = board[index].isEmpty() && isUserTurn && winner == null) {
                                    board[index] = "X"
                                    val winResult = checkWinner()
                                    if (winResult != null) {
                                        winner = winResult
                                        if (winResult == "X") {
                                            score += 30
                                            onSaveScore(score)
                                        }
                                    } else {
                                        isUserTurn = false
                                        makeAiMove()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = board[index],
                                color = if (board[index] == "X") GlowingCyan else LaserPink,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                for (i in 0..8) board[i] = ""
                winner = null
                isUserTurn = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Main Ronde Baru")
        }
    }
}
