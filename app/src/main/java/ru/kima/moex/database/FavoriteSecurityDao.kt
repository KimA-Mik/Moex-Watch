package ru.kima.moex.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.kima.moex.model.FAVORITE_SECURITIES_TABLE_NAME
import ru.kima.moex.model.SecurityEntity

@Dao
interface FavoriteSecurityDao {

    @Query("SELECT * FROM $FAVORITE_SECURITIES_TABLE_NAME")
    suspend fun getAllFavorites(): List<SecurityEntity>

    @Insert
    suspend fun addToFavorite(securityEntity: SecurityEntity)

    @Query("DELETE FROM $FAVORITE_SECURITIES_TABLE_NAME WHERE SECID = :secId")
    suspend fun deleteFromFavorite(secId: String)

    @Query("SELECT EXISTS (SELECT 1 FROM $FAVORITE_SECURITIES_TABLE_NAME WHERE SECID = :secId)")
    suspend fun isSecurityFavorite(secId: String): Boolean
}