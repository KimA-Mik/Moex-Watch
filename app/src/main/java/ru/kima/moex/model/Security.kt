package ru.kima.moex.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


data class Security(
    val SECID: String,
    val SECNAME: String,
    val WAPRICE: Double,
    val LASTCHANGE: Double,
    val LASTCHANGEPRCNT: Double
)

data class SecurityDayPrice(
    val date: Date,
    val price: Double,
    val lowPrice: Double,
    val highPrice: Double,
    val openPrice: Double,
    val closePrice: Double
)

const val FAVORITE_SECURITIES_TABLE_NAME = "favorite_securities"

@Entity(tableName = FAVORITE_SECURITIES_TABLE_NAME)
data class SecurityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sec_id: String,
    val sec_name: String,
    val price: Double,
    val change_percent: Double,
    val is_tracked: Boolean
)