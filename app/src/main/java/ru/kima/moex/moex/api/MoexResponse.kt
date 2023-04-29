package ru.kima.moex.moex.api

import android.icu.text.SimpleDateFormat
import kotlinx.serialization.json.*

class MoexResponse {
    companion object {
        fun parseFromJson(src: String): MutableList<MoexTable> {

            val res = mutableListOf<MoexTable>()
            val root = Json.parseToJsonElement(src)


            for (table in root.jsonObject) {
                try {

                    val tempTable = MoexTable()
                    tempTable.name = table.key
                    val cols = table.value.jsonObject["columns"]?.jsonArray

                    if (cols != null) {
                        for (colName in cols) {
                            tempTable.fields.add(Json.decodeFromJsonElement<String>(colName))
                        }
                    } else {
                        continue
                    }

                    val data = table.value.jsonObject["data"]?.jsonArray
                    val meta = table.value.jsonObject["metadata"]
                    if (data != null && meta != null) {
                        for (record in data) {
                            val tempMap = mutableMapOf<String, Any>()
                            for ((index, recordCol) in record.jsonArray.withIndex()) {
                                val name = tempTable.fields[index]
                                val type = Json.decodeFromJsonElement<String>(
                                    meta.jsonObject[name]?.jsonObject?.get("type")!!
                                )

                                tempMap[name] = (if (type == "int32") {
                                    if (recordCol.jsonPrimitive.intOrNull != null)
                                        Json.decodeFromJsonElement<Int>(recordCol)
                                    else
                                        0
                                } else if (type == "string") {
                                    if (recordCol.jsonPrimitive.isString) {
                                        Json.decodeFromJsonElement<String>(recordCol)
                                    } else {
                                        String()
                                    }
                                } else if (type == "double") {
                                    if (recordCol.jsonPrimitive.doubleOrNull != null)
                                        Json.decodeFromJsonElement<Double>(recordCol)
                                    else
                                        Double.NaN
                                } else if (type == "int64") {
                                    if (recordCol.jsonPrimitive.longOrNull != null)
                                        Json.decodeFromJsonElement<Long>(recordCol)
                                    else
                                        0L
                                } else if (type == "date") {
                                    if (recordCol.jsonPrimitive.isString) {
                                        val dateString =
                                            Json.decodeFromJsonElement<String>(recordCol)
                                        val parser = SimpleDateFormat("yyyy-MM-dd")
                                        parser.parse(dateString)
                                    } else {
                                        String()
                                    }
                                } else {
                                    String()
                                })

                            }
                            tempTable.data.add(tempMap)

                        }
                    } else {
                        continue
                    }

                    res.add(tempTable)
                } catch (e: Exception) {
                    println(e.message)
                }
            }

            return res
        }
    }
}