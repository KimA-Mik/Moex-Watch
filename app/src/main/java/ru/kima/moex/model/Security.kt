package ru.kima.moex.model

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
