package ru.kima.moex.model

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.kima.moex.database.FavoriteSecurityDatabase


private const val DATABASE_NAME = "security-database"

class DatabaseSecurityService(
    context: Context
) {
    private val favoriteSecurityDatabase: FavoriteSecurityDatabase = Room
        .databaseBuilder(
            context,
            FavoriteSecurityDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    suspend fun getAllFavorites(): Flow<List<SecurityEntity>> = flow {
        val result = favoriteSecurityDatabase.favoriteSecurityDao().getAllFavorites()
        emit(result)
    }.flowOn(Dispatchers.IO)

    suspend fun addToFavorite(securityEntity: SecurityEntity) =
        favoriteSecurityDatabase.favoriteSecurityDao().addToFavorite(securityEntity)

    suspend fun deleteFromFavorite(secId: String) =
        favoriteSecurityDatabase.favoriteSecurityDao().deleteFromFavorite(secId)

    suspend fun isSecurityFavorite(secId: String): Boolean =
        favoriteSecurityDatabase.favoriteSecurityDao().isSecurityFavorite(secId)
}