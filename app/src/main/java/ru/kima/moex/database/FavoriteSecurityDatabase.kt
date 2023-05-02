package ru.kima.moex.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kima.moex.model.SecurityEntity

@Database(entities = [SecurityEntity::class], version = 1)
abstract class FavoriteSecurityDatabase : RoomDatabase() {
    abstract fun favoriteSecurityDao(): FavoriteSecurityDao
}