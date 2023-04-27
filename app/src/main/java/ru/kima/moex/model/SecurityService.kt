package ru.kima.moex.model

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import ru.kima.moex.InvalidResponse
import ru.kima.moex.moex.api.MoexApi
import ru.kima.moex.moex.api.MoexResponse

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
        val response = moexApi.fetchSecurities()
        val tables = MoexResponse.ParseFromJson(response)

        if (tables[0].data.size != tables[1].data.size) {
            throw InvalidResponse()
        }
        val result = mutableListOf<Security>()
        for (i in tables[0].data.indices) {
            val security = Security(
                SECID = tables[0].data[i]["SECID"].toString(),
                SECNAME = tables[0].data[i]["SECNAME"].toString(),
                WAPRICE = tables[1].data[i]["WAPRICE"] as Double
            )
            result += security
        }
        return result
    }
}