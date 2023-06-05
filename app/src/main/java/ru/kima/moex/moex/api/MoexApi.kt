package ru.kima.moex.moex.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoexApi {
    @GET(
        "engines/{engine}/" +
                "markets/{market}/" +
                "securities.json"
    )
    suspend fun fetchSecurities(
        @Path("engine") engine: String,
        @Path("market") market: String
    ): String

    @GET(
        "engines/{engine}/" +
                "markets/{market}/" +
                "securities/{security}.json"
    )
    suspend fun fetchSecurity(
        @Path("engine") engine: String,
        @Path("market") market: String,
        @Path("security") secId: String,
    ): String

    @GET(
        "history/" +
                "engines/{engine}/" +
                "markets/{market}/" +
                "sessions/{session}/" +
                "securities/{security}.json"
    )
    suspend fun getSecurityPriceHistoryFrom(
        @Path("engine") engine: String,
        @Path("market") market: String,
        @Path("session") session: Int,
        @Path("security") secId: String,
        @Query("from") from: String,
        @Query("start") index: Long
    ): String
}