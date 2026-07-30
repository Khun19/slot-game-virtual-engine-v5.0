package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SlotGameEntity::class], version = 1, exportSchema = false)
abstract class SlotGameDatabase : RoomDatabase() {
    abstract fun slotGameDao(): SlotGameDao

    companion object {
        @Volatile
        private var INSTANCE: SlotGameDatabase? = null

        fun getDatabase(context: Context): SlotGameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SlotGameDatabase::class.java,
                    "slot_games_library.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
