package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.GameScoreEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.GlowingCyan
import com.example.ui.theme.LaserPink
import com.example.ui.theme.MvpGold
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceCardBg
import com.example.ui.theme.SpaceDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class GameItemInfo(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun GameHubScreen(
    userProfile: UserProfileEntity?,
    gameScores: List<GameScoreEntity>,
    onLaunchGame: (String) -> Unit,
    onOpenPaywallModal: () -> Unit
) {
    val accessCount = userProfile?.accessCount ?: 0
    val isMvp = userProfile?.isMvp ?: false

    val gamesList = listOf(
        GameItemInfo(
            id = "space_shooter",
            title = "Perang Antariksa",
            category = "Arcade Shooter",
            description = "Tembak asteroid & musuh antariksa dengan laser!",
            icon = Icons.Default.RocketLaunch,
            color = LaserPink
        ),
        GameItemInfo(
            id = "snake",
            title = "Ular Galaksi Retro",
            category = "Classic Retro",
            description = "Santap bintang glowing dan perpanjang ular neon!",
            icon = Icons.Default.VideogameAsset,
            color = GlowingCyan
        ),
        GameItemInfo(
            id = "quiz",
            title = "Kuis Pintar Galaksi",
            category = "Trivia AI",
            description = "Uji pengetahuan sains, koding, dan luar angkasa!",
            icon = Icons.Default.Psychology,
            color = MvpGold
        ),
        GameItemInfo(
            id = "tictactoe",
            title = "Silang Lingkaran AI",
            category = "Strategy",
            description = "Tantang kecerdasan Galaksi AI di papan 3x3!",
            icon = Icons.Default.GridOn,
            color = NeonViolet
        ),
        GameItemInfo(
            id = "word_master",
            title = "Kata Galaksi",
            category = "Word Puzzle",
            description = "Tebak 5 huruf istilah teknologi antariksa!",
            icon = Icons.Default.Spellcheck,
            color = GlowingCyan
        ),
        GameItemInfo(
            id = "rpg",
            title = "Petualangan Teks RPG",
            category = "Text Adventure",
            description = "Tentukan jalan petualangan antariksa milikmu!",
            icon = Icons.Default.AutoAwesome,
            color = LaserPink
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDarkBg)
            .padding(16.dp)
    ) {
        // Hero Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, NeonViolet, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SpaceCardBg)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_galaksi_hero),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(SpaceBlack.copy(alpha = 0.95f), Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🎮 Galaksi Arcade Hub",
                        color = MvpGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "6 Game Antariksa Bawaan • BISA DIENJOY TANPA DOWNLOAD!",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quota Bar Notice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SpaceBlack)
                .clickable { if (!isMvp) onOpenPaywallModal() }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isMvp) Icons.Default.Star else Icons.Default.Lock,
                    contentDescription = null,
                    tint = MvpGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMvp) "Status MVP VIP: Main Game Sepuasnya!" else "Akses Main: $accessCount/2 Terpakai",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!isMvp) {
                Text(
                    text = "Upgrade Rp 50.000 ►",
                    color = MvpGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Game Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(gamesList) { game ->
                val scoreEntity = gameScores.find { it.gameId == game.id }
                val highScore = scoreEntity?.highScore ?: 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SpaceCardBg, RoundedCornerShape(16.dp))
                        .clickable { onLaunchGame(game.id) }
                        .testTag("game_card_${game.id}"),
                    colors = CardDefaults.cardColors(containerColor = SpaceCardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(game.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = game.icon,
                                    contentDescription = null,
                                    tint = game.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SpaceBlack)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🏆 $highScore",
                                    color = MvpGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = game.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = game.category,
                            color = game.color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = game.description,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            maxLines = 2,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onLaunchGame(game.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("play_game_btn_${game.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = game.color)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("MAIN", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
