package ru.kima.moex.moex.api

import retrofit2.http.GET

interface MoexApi {

    @GET("engines/stock/" +
            "markets/shares/" +
            "securities.json")
    suspend fun fetchSecurities(): String
}