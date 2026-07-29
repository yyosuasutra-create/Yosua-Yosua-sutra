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
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.ui.theme.MvpGold
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceCardBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Composable
fun QuizGame(
    onBack: () -> Unit,
    onSaveScore: (Int) -> Unit
) {
    val questions = remember {
        listOf(
            QuizQuestion(
                question = "Planet manakah yang dikenal sebagai 'Planet Merah' di sistem Tata Surya?",
                options = listOf("Venus", "Mars", "Jupiter", "Saturnus"),
                correctIndex = 1,
                explanation = "Mars terlihat merah karena tingginya kandungan zat besi oksida (karat) di permukaannya."
            ),
            QuizQuestion(
                question = "Berapa kecepatan cahaya di ruang hampa udara?",
                options = listOf("300.000 km/detik", "150.000 km/detik", "1.000.000 km/detik", "30.000 km/detik"),
                correctIndex = 0,
                explanation = "Cahaya merambat pada kecepatan konstan sekitar 299.792 km/detik di hampa antariksa."
            ),
            QuizQuestion(
                question = "Bahasa pemrograman utama yang digunakan secara resmi untuk Android Jetpack Compose adalah?",
                options = listOf("Java", "Python", "Kotlin", "Swift"),
                correctIndex = 2,
                explanation = "Kotlin adalah bahasa resmi yang dianjurkan oleh Google untuk pengembangan Android modern."
            ),
            QuizQuestion(
                question = "Apakah galaksi terdekat dari galaksi Bima Sakti (Milky Way)?",
                options = listOf("Andromeda", "Sombrero", "Triangulum", "Centaurus A"),
                correctIndex = 0,
                explanation = "Galaksi Andromeda berjarak sekitar 2.5 juta tahun cahaya dari Bima Sakti."
            )
        )
    }

    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_quiz")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Text("KUIS PINTAR GALAKSI", color = MvpGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Skor: $score", color = GlowingCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isFinished) {
            val q = questions[currentIndex]

            Text(
                text = "Pertanyaan ${currentIndex + 1} dari ${questions.size}",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = q.question,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            q.options.forEachIndexed { index, optionText ->
                val isSelected = selectedOption == index
                val isCorrect = index == q.correctIndex
                val showAnswer = selectedOption != null

                val bgColor = when {
                    showAnswer && isCorrect -> Color(0xFF1B4332)
                    showAnswer && isSelected && !isCorrect -> Color(0xFF590D22)
                    isSelected -> NeonViolet
                    else -> SpaceCardBg
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(1.dp, if (isSelected) MvpGold else SpaceCardBg, RoundedCornerShape(12.dp))
                        .clickable(enabled = selectedOption == null) {
                            selectedOption = index
                            if (index == q.correctIndex) {
                                score += 25
                            }
                        }
                        .padding(16.dp)
                ) {
                    Text(
                        text = "${'A' + index}. $optionText",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            if (selectedOption != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceCardBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("💡 Penjelasan Galaksi 70:", color = GlowingCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(q.explanation, color = TextSecondary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedOption = null
                        if (currentIndex + 1 < questions.size) {
                            currentIndex++
                        } else {
                            isFinished = true
                            onSaveScore(score)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                ) {
                    Text(if (currentIndex + 1 < questions.size) "Pertanyaan Berikutnya ►" else "Selesai Kuis")
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MvpGold, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("KUIS SELESAI!", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Skor Akhir Anda: $score / 100", color = GlowingCyan, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            currentIndex = 0
                            score = 0
                            selectedOption = null
                            isFinished = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ulangi Kuis")
                    }
                }
            }
        }
    }
}
