package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String, // "BACKUP", "RESTORE", "AUTO_SYNC"
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
