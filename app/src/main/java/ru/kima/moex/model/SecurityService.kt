package ru.kima.moex.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import ru.kima.moex.InvalidResponse
import ru.kima.moex.moex.api.MoexApi
import ru.kima.moex.moex.api.MoexResponse
import ru.kima.moex.moex.api.MoexTable
import ru.kima.moex.moex.api.QueryConverterFactory
import java.util.Date

class SecurityService {
    private val moexApi: MoexApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://iss.moex.com/iss/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(QueryConverterFactory.create())
            .build()
        moexApi = retrofit.create()
    }

    suspend fun fetchSecurities(): List<Security> {
        val response = moexApi.fetchSecurities()
        val tables = MoexResponse.parseFromJson(response)

        if (tables[0].data.size != tables[1].data.size) {
            throw InvalidResponse()
        }
        val result = mutableListOf<Security>()
        for (i in tables[0].data.indices) {
            if ((tables[1].data[i]["WAPRICE"] as Double).isNaN())
                continue
            val security = Security(
                SECID = tables[0].data[i]["SECID"].toString(),
                SECNAME = tables[0].data[i]["SECNAME"].toString(),
                WAPRICE = tables[1].data[i]["WAPRICE"] as Double
            )
            result += security
        }
        return result
    }

    suspend fun getSecurityPriceHistoryFrom(
        secId: String,
        from: Date
    ): Flow<List<SecurityDayPrice>> = flow {
        var index = 0L
        var response = moexApi.getSecurityPriceHistoryFrom(secId, from, index)
        var tables = MoexResponse.parseFromJson(response)
        var result = parsePriceHistoryFromMoexTable(tables[0])
        emit(result)

        val totalRecords = tables[1].data[0]["TOTAL"] as Long
        val pageSize = tables[1].data[0]["PAGESIZE"] as Long

        while (index + pageSize < totalRecords) {
            index += pageSize
            response = moexApi.getSecurityPriceHistoryFrom(secId, from, index)
            tables = MoexResponse.parseFromJson(response)
            result = parsePriceHistoryFromMoexTable(tables[0])
            emit(result)
        }
    }.flowOn(Dispatchers.IO)

    private fun parsePriceHistoryFromMoexTable(table: MoexTable): List<SecurityDayPrice> {
        val result = mutableListOf<SecurityDayPrice>()
        for (secItem in table.data) {
            val sec = SecurityDayPrice(
                date = secItem["TRADEDATE"] as Date,
                price = secItem["WAPRICE"] as Double
            )
            result += sec
        }
        return result
    }
}