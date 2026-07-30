package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SlotGameDao {
    @Query("SELECT * FROM slot_games ORDER BY isFavorite DESC, lastLaunchedTimestamp DESC, appName ASC")
    fun getAllGames(): Flow<List<SlotGameEntity>>

    @Query("SELECT * FROM slot_games WHERE packageName = :packageName")
    suspend fun getGameByPackage(packageName: String): SlotGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: SlotGameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<SlotGameEntity>)

    @Query("DELETE FROM slot_games WHERE packageName = :packageName")
    suspend fun deleteGameByPackage(packageName: String)

    @Query("UPDATE slot_games SET lastLaunchedTimestamp = :timestamp, playCount = playCount + 1, isRunningInSandbox = 1 WHERE packageName = :packageName")
    suspend fun recordGameLaunch(packageName: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE slot_games SET isRunningInSandbox = 0 WHERE packageName = :packageName")
    suspend fun recordGameStop(packageName: String)

    @Query("UPDATE slot_games SET isRunningInSandbox = 0")
    suspend fun stopAllGames()

    @Query("UPDATE slot_games SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun setFavorite(packageName: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM slot_games")
    suspend fun getGameCount(): Int
}
