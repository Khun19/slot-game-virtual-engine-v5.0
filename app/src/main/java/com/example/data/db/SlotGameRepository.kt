package com.example.data.db

import kotlinx.coroutines.flow.Flow

class SlotGameRepository(private val dao: SlotGameDao) {
    val allGames: Flow<List<SlotGameEntity>> = dao.getAllGames()

    suspend fun getGameByPackage(packageName: String): SlotGameEntity? = dao.getGameByPackage(packageName)

    suspend fun insertGame(game: SlotGameEntity) = dao.insertGame(game)

    suspend fun insertGames(games: List<SlotGameEntity>) = dao.insertGames(games)

    suspend fun deleteGame(packageName: String) = dao.deleteGameByPackage(packageName)

    suspend fun recordLaunch(packageName: String) = dao.recordGameLaunch(packageName)

    suspend fun recordStop(packageName: String) = dao.recordGameStop(packageName)

    suspend fun stopAll() = dao.stopAllGames()

    suspend fun toggleFavorite(packageName: String, currentFavorite: Boolean) = dao.setFavorite(packageName, !currentFavorite)

    suspend fun getGameCount(): Int = dao.getGameCount()
}
