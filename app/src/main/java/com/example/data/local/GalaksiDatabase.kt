package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChatEntity::class,
        GameScoreEntity::class,
        UserProfileEntity::class,
        SyncLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GalaksiDatabase : RoomDatabase() {

    abstract fun galaksiDao(): GalaksiDao

    companion object {
        @Volatile
        private var INSTANCE: GalaksiDatabase? = null

        fun getDatabase(context: Context): GalaksiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GalaksiDatabase::class.java,
                    "galaksi_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
