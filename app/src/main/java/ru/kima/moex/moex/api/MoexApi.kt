package ru.kima.moex.moex.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoexApi {

    @GET(
        "engines/stock/" +
                "markets/shares/" +
                "securities.json"
    )
    suspend fun fetchSecurities(): String

    @GET(
        "history/" +
                "engines/stock/" +
                "markets/shares/" +
                "sessions/1/" +
                "securities/{security}.json"
    )
    suspend fun getSecurityPriceHistoryFrom(
        @Path("security") secId: String,
        @Query("from") from: String,
        @Query("start") index: Long
    ): String
}