package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.CloudSyncScreen
import com.example.ui.screens.GalaksiAiScreen
import com.example.ui.screens.GameHubScreen
import com.example.ui.screens.MvpPaywallModal
import com.example.ui.screens.MvpPaywallScreen
import com.example.ui.screens.games.QuizGame
import com.example.ui.screens.games.RpgAdventureGame
import com.example.ui.screens.games.SnakeGame
import com.example.ui.screens.games.SpaceShooterGame
import com.example.ui.screens.games.TicTacToeGame
import com.example.ui.screens.games.WordMasterGame
import com.example.ui.theme.GalaksiTheme
import com.example.ui.theme.GlowingCyan
import com.example.ui.theme.MvpGold
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GalaksiViewModel

data class NavTabItem(val title: String, val icon: ImageVector, val tag: String)

class MainActivity : ComponentActivity() {

    private val viewModel: GalaksiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GalaksiTheme {
                GalaksiAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GalaksiAppContent(viewModel: GalaksiViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val gameScores by viewModel.gameScores.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()
    val chatState by viewModel.chatState.collectAsStateWithLifecycle()
    val showPaywallModal by viewModel.showPaywallModal.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var activeGameId by remember { mutableStateOf<String?>(null) }

    val navTabs = listOf(
        NavTabItem("Galaksi AI", Icons.Default.AutoAwesome, "tab_ai_chat"),
        NavTabItem("Game Hub", Icons.Default.Gamepad, "tab_game_hub"),
        NavTabItem("Cloud Sync", Icons.Default.CloudSync, "tab_cloud_sync"),
        NavTabItem("MVP VIP", Icons.Default.Star, "tab_mvp")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (activeGameId == null) {
                NavigationBar(
                    containerColor = SpaceBlack,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp
                ) {
                    navTabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            modifier = Modifier.testTag(tab.tag),
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) {
                                        if (index == 3) MvpGold else GlowingCyan
                                    } else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = NeonViolet.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SpaceDarkBg)
        ) {
            if (activeGameId != null) {
                when (activeGameId) {
                    "space_shooter" -> SpaceShooterGame(
                        onBack = { activeGameId = null },
                        onSaveScore = { score -> viewModel.saveGameScore("space_shooter", "Perang Antariksa", score) }
                    )
                    "snake" -> SnakeGame(
                        onBack = { activeGameId = null },
                        onSaveScore = { score -> viewModel.saveGameScore("snake", "Ular Galaksi Retro", score) }
                    )
                    "quiz" -> QuizGame(
                        onBack = { activeGameId = null },
                        onSaveScore = { score -> viewModel.saveGameScore("quiz", "Kuis Pintar Galaksi", score) }
                    )
                    "tictactoe" -> TicTacToeGame(
                        onBack = { activeGameId = null },
                        onSaveScore = { score -> viewModel.saveGameScore("tictactoe", "Silang Lingkaran AI", score) }
                    )
                    "word_master" -> WordMasterGame(
                        onBack = { activeGameId = null },
                        onSaveScore = { score -> viewModel.saveGameScore("word_master", "Kata Galaksi", score) }
                    )
                    "rpg" -> RpgAdventureGame(
                        onBack = { activeGameId = null },
                        onSaveScore = { score -> viewModel.saveGameScore("rpg", "Petualangan Teks RPG", score) }
                    )
                }
            } else {
                when (selectedTab) {
                    0 -> GalaksiAiScreen(
                        userProfile = userProfile,
                        chats = chats,
                        chatState = chatState,
                        onSendMessage = { prompt -> viewModel.sendChatMessage(prompt) },
                        onOpenPaywallModal = { viewModel.openPaywallModal() }
                    )
                    1 -> GameHubScreen(
                        userProfile = userProfile,
                        gameScores = gameScores,
                        onLaunchGame = { gameId ->
                            viewModel.checkAndStartGame(gameId) {
                                activeGameId = gameId
                            }
                        },
                        onOpenPaywallModal = { viewModel.openPaywallModal() }
                    )
                    2 -> CloudSyncScreen(
                        userProfile = userProfile,
                        syncLogs = syncLogs,
                        isSyncing = isSyncing,
                        syncMessage = syncMessage,
                        onSyncNow = { viewModel.syncCloudProgress() },
                        onRestoreNow = { viewModel.restoreCloudProgress() },
                        onToggleAutoSync = { enabled -> viewModel.toggleCloudSync(enabled) }
                    )
                    3 -> MvpPaywallScreen(
                        userProfile = userProfile,
                        onActivateMvp = { viewModel.activateMvpPayment() }
                    )
                }
            }

            // MVP Modal Overlay
            if (showPaywallModal) {
                MvpPaywallModal(
                    accessCount = userProfile?.accessCount ?: 0,
                    isMvp = userProfile?.isMvp ?: false,
                    onDismiss = { viewModel.closePaywallModal() },
                    onConfirmPayment = { viewModel.activateMvpPayment() }
                )
            }
        }
    }
}
