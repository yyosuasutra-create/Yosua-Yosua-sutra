package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GalaksiDao {

    // Chat queries
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChats()

    // Game scores queries
    @Query("SELECT * FROM game_scores")
    fun getAllGameScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT * FROM game_scores WHERE gameId = :gameId")
    suspend fun getGameScore(gameId: String): GameScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGameScore(score: GameScoreEntity)

    // User Profile queries
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileDirect(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    // Sync logs queries
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT 20")
    fun getSyncLogs(): Flow<List<SyncLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncLog(log: SyncLogEntity)
}
