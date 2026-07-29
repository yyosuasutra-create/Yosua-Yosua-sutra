package com.example.data.repository

import com.example.data.local.ChatEntity
import com.example.data.local.GalaksiDao
import com.example.data.local.GameScoreEntity
import com.example.data.local.SyncLogEntity
import com.example.data.local.UserProfileEntity
import com.example.data.remote.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class GalaksiRepository(
    private val dao: GalaksiDao,
    private val geminiService: GeminiService = GeminiService()
) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allChats: Flow<List<ChatEntity>> = dao.getAllChats()
    val gameScores: Flow<List<GameScoreEntity>> = dao.getAllGameScores()
    val syncLogs: Flow<List<SyncLogEntity>> = dao.getSyncLogs()

    suspend fun ensureProfileExists(): UserProfileEntity = withContext(Dispatchers.IO) {
        var profile = dao.getUserProfileDirect()
        if (profile == null) {
            profile = UserProfileEntity(
                id = 1,
                userName = "Penjelajah Galaksi",
                accessCount = 0,
                isMvp = false,
                cloudSyncEnabled = true,
                lastSyncTimestamp = System.currentTimeMillis()
            )
            dao.insertUserProfile(profile)
        }
        profile
    }

    suspend fun canAccessFeature(): Boolean = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        if (profile.isMvp) return@withContext true
        return@withContext profile.accessCount < 2
    }

    suspend fun incrementUsageCount(): UserProfileEntity = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        val updated = profile.copy(accessCount = profile.accessCount + 1)
        dao.insertUserProfile(updated)
        updated
    }

    suspend fun sendChatMessage(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        
        // Quota check: 2 free accesses allowed if not MVP
        if (!profile.isMvp && profile.accessCount >= 2) {
            return@withContext Result.failure(
                QuotaExceededException("Batas penggunaan gratis 2x telah dicapai. Diperlukan Akses MVP VIP Rp 50.000!")
            )
        }

        // Save User Message
        dao.insertChat(ChatEntity(sender = "user", message = prompt))
        incrementUsageCount()

        // Get AI Response
        val aiResponse = geminiService.generateResponse(prompt)
        
        // Save AI Message
        dao.insertChat(ChatEntity(sender = "ai", message = aiResponse))

        // Auto Sync if enabled
        if (profile.cloudSyncEnabled) {
            autoSyncInternal("Pesan AI tersimpan & disinkronkan")
        }

        Result.success(aiResponse)
    }

    suspend fun recordGamePlayed(gameId: String): Boolean = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        if (!profile.isMvp && profile.accessCount >= 2) {
            return@withContext false
        }
        incrementUsageCount()
        true
    }

    suspend fun saveGameScore(gameId: String, gameTitle: String, newScore: Int) = withContext(Dispatchers.IO) {
        val existing = dao.getGameScore(gameId)
        val currentHighScore = existing?.highScore ?: 0
        val gamesPlayed = (existing?.gamesPlayed ?: 0) + 1
        val finalHighScore = maxOf(currentHighScore, newScore)

        val updatedScore = GameScoreEntity(
            gameId = gameId,
            gameTitle = gameTitle,
            highScore = finalHighScore,
            gamesPlayed = gamesPlayed,
            lastPlayedTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateGameScore(updatedScore)

        val profile = ensureProfileExists()
        if (profile.cloudSyncEnabled) {
            autoSyncInternal("Skor game $gameTitle ($newScore) tersimpan ke Cloud")
        }
    }

    suspend fun activateMvp(): UserProfileEntity = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        val updated = profile.copy(isMvp = true)
        dao.insertUserProfile(updated)
        
        dao.insertSyncLog(
            SyncLogEntity(
                action = "MVP_ACTIVATED",
                status = "SUCCESS",
                details = "Akses MVP VIP Rp 50.000 Berhasil Diaktifkan! Fitur Unlimited unlocked."
            )
        )
        updated
    }

    suspend fun toggleCloudSync(enabled: Boolean) = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        val updated = profile.copy(cloudSyncEnabled = enabled)
        dao.insertUserProfile(updated)
    }

    suspend fun syncCloudProgress(): String = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        val chats = dao.getAllChats().firstOrNull()?.size ?: 0
        val scores = dao.getAllGameScores().firstOrNull()?.size ?: 0

        val now = System.currentTimeMillis()
        val updatedProfile = profile.copy(lastSyncTimestamp = now)
        dao.insertUserProfile(updatedProfile)

        val logDetails = "Progres $chats obrolan, $scores statistik game & status MVP disinkronkan ke Server Galaksi Cloud."
        dao.insertSyncLog(
            SyncLogEntity(
                action = "MANUAL_SYNC",
                status = "SUCCESS",
                details = logDetails,
                timestamp = now
            )
        )

        logDetails
    }

    suspend fun restoreCloudProgress(): String = withContext(Dispatchers.IO) {
        val profile = ensureProfileExists()
        val now = System.currentTimeMillis()

        dao.insertSyncLog(
            SyncLogEntity(
                action = "RESTORE",
                status = "SUCCESS",
                details = "Semua histori obrolan dan skor game berhasil dipulihkan dari Galaksi Cloud Server.",
                timestamp = now
            )
        )

        "Pemulihan Cloud Berhasil! Progres Galaksi AI telah dipulihkan."
    }

    private suspend fun autoSyncInternal(reason: String) {
        val now = System.currentTimeMillis()
        val profile = dao.getUserProfileDirect()
        if (profile != null) {
            dao.insertUserProfile(profile.copy(lastSyncTimestamp = now))
            dao.insertSyncLog(
                SyncLogEntity(
                    action = "AUTO_SYNC",
                    status = "SUCCESS",
                    details = reason,
                    timestamp = now
                )
            )
        }
    }

    suspend fun clearAllChats() = withContext(Dispatchers.IO) {
        dao.clearChats()
    }
}

class QuotaExceededException(message: String) : Exception(message)
