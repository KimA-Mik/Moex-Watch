package ru.kima.moex.model

import android.text.format.DateFormat
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
import java.util.Date

private const val DEFAULT_BOARD_ID = "TQBR"

class SecurityService {
    private val moexApi: MoexApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://iss.moex.com/iss/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        moexApi = retrofit.create()
    }

    suspend fun fetchSecurities(): List<Security> {
        val response = moexApi.fetchSecurities("stock", "shares")
        val tables = MoexResponse.parseFromJson(response)
        val map = mutableMapOf<String, Int>()

        if (tables[0].data.size != tables[1].data.size) {
            throw InvalidResponse()
        }
        val result = mutableListOf<Security>()
        for (i in tables[0].data.indices) {
            if ((tables[1].data[i]["WAPRICE"] as Double).isNaN())
                continue


            val name = tables[0].data[i]["SECID"].toString()
            if (map.contains(name)) {
                val index = map[name]!!
                if (result[index].LASTCHANGE == 0.0) {
                    result[index] = result[index].copy(
                        LASTCHANGE = tables[1].data[i]["LASTCHANGE"] as Double,
                        LASTCHANGEPRCNT = tables[1].data[i]["LASTCHANGEPRCNT"] as Double
                    )
                }
                continue
            } else {
                map[name] = result.size
            }


            val security = Security(
                SECID = tables[0].data[i]["SECID"].toString(),
                SECNAME = tables[0].data[i]["SECNAME"].toString(),
                WAPRICE = tables[1].data[i]["WAPRICE"] as Double,
                LASTCHANGE = tables[1].data[i]["LASTCHANGE"] as Double,
                LASTCHANGEPRCNT = tables[1].data[i]["LASTCHANGEPRCNT"] as Double
            )
            result += security
        }
        return result
    }

    suspend fun fetchSecurity(secId: String): SecurityEntity {
        val response = moexApi.fetchSecurity("stock", "shares", secId)
        val tables = MoexResponse.parseFromJson(response)
        var defaultBoardId = -1
        for (i in tables[0].data.indices) {
            if (tables[0].data[i]["BOARDID"].toString() == DEFAULT_BOARD_ID) {
                defaultBoardId = i
                break
            }
        }

        if (defaultBoardId == -1)
            throw InvalidResponse()

        val secName = tables[0].data[defaultBoardId]["SHORTNAME"] as String
        val price = tables[1].data[defaultBoardId]["WAPRICE"] as Double
        val changed = tables[1].data[defaultBoardId]["LASTTOPREVPRICE"] as Double

        return SecurityEntity(
            0, sec_id = secId, sec_name = secName,
            price, changed, false
        )
    }

    suspend fun getSecurityPriceHistoryFrom(
        secId: String,
        from: Date
    ): Flow<List<SecurityDayPrice>> = flow {
        var index = 0L
        val date = DateFormat.format("yyyy-MM-dd", from).toString()
        var response = moexApi.getSecurityPriceHistoryFrom("stock", "shares", 3, secId, date, index)
        var tables = MoexResponse.parseFromJson(response)
        var result = parsePriceHistoryFromMoexTable(tables[0])
        emit(result)

        val totalRecords = tables[1].data[0]["TOTAL"] as Long
        val pageSize = tables[1].data[0]["PAGESIZE"] as Long

        while (index + pageSize < totalRecords) {
            index += pageSize
            response = moexApi.getSecurityPriceHistoryFrom("stock", "shares", 3, secId, date, index)
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
                price = secItem["WAPRICE"] as Double,
                lowPrice = secItem["LOW"] as Double,
                highPrice = secItem["HIGH"] as Double,
                openPrice = secItem["OPEN"] as Double,
                closePrice = secItem["CLOSE"] as Double
            )
            result += sec
        }
        return result
    }
}