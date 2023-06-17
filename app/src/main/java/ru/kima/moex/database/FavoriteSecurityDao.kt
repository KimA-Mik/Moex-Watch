package ru.kima.moex.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.kima.moex.model.FAVORITE_SECURITIES_TABLE_NAME
import ru.kima.moex.model.SecurityEntity

@Dao
interface FavoriteSecurityDao {
    @Query("SELECT * FROM $FAVORITE_SECURITIES_TABLE_NAME WHERE sec_id = :secId")
    suspend fun getSecurityBySecId(secId: String): SecurityEntity?

    @Query("SELECT * FROM $FAVORITE_SECURITIES_TABLE_NAME")
    suspend fun getAllFavorites(): List<SecurityEntity>

    @Insert
    suspend fun addToFavorite(securityEntity: SecurityEntity)

    @Query("DELETE FROM $FAVORITE_SECURITIES_TABLE_NAME WHERE sec_id = :secId")
    suspend fun deleteFromFavorite(secId: String)

    @Query("SELECT EXISTS (SELECT 1 FROM $FAVORITE_SECURITIES_TABLE_NAME WHERE sec_id = :secId)")
    suspend fun isSecurityFavorite(secId: String): Boolean

    @Update
    suspend fun updateSecurity(securityEntity: SecurityEntity)
}