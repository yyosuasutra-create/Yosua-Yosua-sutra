package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "Galaksi Explorer",
    val accessCount: Int = 0, // Number of free queries / games played (Limit is 2)
    val isMvp: Boolean = false, // MVP Status (Rp 50.000 paid)
    val cloudSyncEnabled: Boolean = true,
    val lastSyncTimestamp: Long = 0L,
    val cloudSyncId: String = "GALAKSI-CLOUD-88492"
)
