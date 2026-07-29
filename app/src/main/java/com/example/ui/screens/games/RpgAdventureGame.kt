package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

data class RpgChoice(val optionText: String, val outcomeText: String, val scoreAdd: Int, val nextChoices: List<RpgChoice> = emptyList())

@Composable
fun RpgAdventureGame(
    onBack: () -> Unit,
    onSaveScore: (Int) -> Unit
) {
    var storyText by remember {
        mutableStateOf("Kapal antariksa Anda mendarat darurat di Planet X-9. Sensor mendeteksi sinyal energi misterius di balik jurang kristal. Apa tindakan Anda?")
    }
    var score by remember { mutableStateOf(10) }
    var currentChoices by remember {
        mutableStateOf(
            listOf(
                "🚀 [A] Lakukan pemindaian radar dan masuki jurang kristal.",
                "📡 [B] Kirim sinyal darurat ke stasiun Galaksi AI.",
                "🛡️ [C] Tetap di kapal dan perbaiki pendorong hyperspace."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_from_rpg")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Text("PETUALANGAN TEKS AI RPG", color = MvpGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Exp: $score", color = GlowingCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonViolet, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceCardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📖 Narasi Game Master Galaksi 70:", color = GlowingCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(storyText, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("TENTUKAN PILIHAN ANDA:", color = MvpGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        currentChoices.forEachIndexed { index, choice ->
            Button(
                onClick = {
                    score += 15
                    storyText = when (index) {
                        0 -> "Anda mengaktifkan radar. Sinyal membawa Anda menemukan reaktor energi kuno yang memulihkan cadangan energi kapal sebesar 100%! Anda siap lepas landas."
                        1 -> "Sinyal Anda diterima oleh armada patroli Galaksi AI 70! Tim penyelamat tiba dalam 5 menit dan memberi hadiah modul kapal antariksa baru."
                        else -> "Anda berhasil memperbaiki pendorong hyperspace tepat sebelum badai nebula melintas. Kapal meluncur dengan selamat melintasi galaksi!"
                    }
                    currentChoices = emptyList()
                    onSaveScore(score)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpaceCardBg)
            ) {
                Text(choice, color = TextPrimary, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        if (currentChoices.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    storyText = "Kapal antariksa Anda mendarat darurat di Planet X-9. Sensor mendeteksi sinyal energi misterius di balik jurang kristal. Apa tindakan Anda?"
                    score = 10
                    currentChoices = listOf(
                        "🚀 [A] Lakukan pemindaian radar dan masuki jurang kristal.",
                        "📡 [B] Kirim sinyal darurat ke stasiun Galaksi AI.",
                        "🛡️ [C] Tetap di kapal dan perbaiki pendorong hyperspace."
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mulai Misi Baru")
            }
        }
    }
}
