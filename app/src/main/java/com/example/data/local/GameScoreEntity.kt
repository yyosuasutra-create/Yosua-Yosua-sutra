package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey
    val gameId: String, // "space_shooter", "snake", "quiz", "tictactoe", "word_master", "rpg"
    val gameTitle: String,
    val highScore: Int,
    val gamesPlayed: Int,
    val lastPlayedTimestamp: Long = System.currentTimeMillis()
)
