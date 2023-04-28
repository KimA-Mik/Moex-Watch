package ru.kima.moex.model

import java.util.Date


data class Security(
    val SECID: String,
    val SECNAME: String,
    val WAPRICE: Double
)

data class SecurityDayPrice(
    val date: Date,
    val price: Double
)
