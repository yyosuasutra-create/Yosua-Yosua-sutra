package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatEntity
import com.example.data.local.GalaksiDatabase
import com.example.data.local.GameScoreEntity
import com.example.data.local.SyncLogEntity
import com.example.data.local.UserProfileEntity
import com.example.data.repository.GalaksiRepository
import com.example.data.repository.QuotaExceededException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AiChatState {
    object Idle : AiChatState
    object Loading : AiChatState
    data class Error(val message: String, val isQuotaExceeded: Boolean = false) : AiChatState
}

class GalaksiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GalaksiRepository

    init {
        val dao = GalaksiDatabase.getDatabase(application).galaksiDao()
        repository = GalaksiRepository(dao)
        
        viewModelScope.launch {
            repository.ensureProfileExists()
        }
    }

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val chats: StateFlow<List<ChatEntity>> = repository.allChats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val gameScores: StateFlow<List<GameScoreEntity>> = repository.gameScores.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val syncLogs: StateFlow<List<SyncLogEntity>> = repository.syncLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _chatState = MutableStateFlow<AiChatState>(AiChatState.Idle)
    val chatState: StateFlow<AiChatState> = _chatState.asStateFlow()

    private val _showPaywallModal = MutableStateFlow(false)
    val showPaywallModal: StateFlow<Boolean> = _showPaywallModal.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun sendChatMessage(prompt: String) {
        if (prompt.isBlank()) return

        viewModelScope.launch {
            _chatState.value = AiChatState.Loading
            val result = repository.sendChatMessage(prompt)

            result.onSuccess {
                _chatState.value = AiChatState.Idle
            }.onFailure { exception ->
                if (exception is QuotaExceededException) {
                    _chatState.value = AiChatState.Error(
                        message = exception.message ?: "Kuota gratis telah habis. Upgrade ke MVP Rp 50.000!",
                        isQuotaExceeded = true
                    )
                    _showPaywallModal.value = true
                } else {
                    _chatState.value = AiChatState.Error(
                        message = exception.message ?: "Gagal memproses pesan AI Galaksi.",
                        isQuotaExceeded = false
                    )
                }
            }
        }
    }

    fun checkAndStartGame(gameId: String, onCanPlay: () -> Unit) {
        viewModelScope.launch {
            val allowed = repository.recordGamePlayed(gameId)
            if (allowed) {
                onCanPlay()
            } else {
                _showPaywallModal.value = true
            }
        }
    }

    fun saveGameScore(gameId: String, gameTitle: String, score: Int) {
        viewModelScope.launch {
            repository.saveGameScore(gameId, gameTitle, score)
        }
    }

    fun activateMvpPayment() {
        viewModelScope.launch {
            repository.activateMvp()
            _showPaywallModal.value = false
            _chatState.value = AiChatState.Idle
        }
    }

    fun openPaywallModal() {
        _showPaywallModal.value = true
    }

    fun closePaywallModal() {
        _showPaywallModal.value = false
    }

    fun syncCloudProgress() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Menghubungkan ke Galaksi Cloud..."
            kotlinx.coroutines.delay(1200) // Realistic cloud latency
            val msg = repository.syncCloudProgress()
            _isSyncing.value = false
            _syncMessage.value = msg
        }
    }

    fun restoreCloudProgress() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Mengunduh cadangan Galaksi Cloud..."
            kotlinx.coroutines.delay(1200)
            val msg = repository.restoreCloudProgress()
            _isSyncing.value = false
            _syncMessage.value = msg
        }
    }

    fun toggleCloudSync(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleCloudSync(enabled)
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    fun clearChats() {
        viewModelScope.launch {
            repository.clearAllChats()
        }
    }
}
