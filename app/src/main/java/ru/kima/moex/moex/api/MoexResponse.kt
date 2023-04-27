package ru.kima.moex.moex.api

import kotlinx.serialization.json.*

class MoexResponse {
    companion object {
        fun ParseFromJson(src: String): MutableList<MoexTable> {

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
                            var index = 0
                            for (recordCol in record.jsonArray) {
                                val name = tempTable.fields[index]
                                val type = Json.decodeFromJsonElement<String>(
                                    meta.jsonObject[name]?.jsonObject?.get("type")!!
                                )

                                tempMap[name] = (if (type.equals("int32")) {
                                    if (recordCol.jsonPrimitive.intOrNull != null)
                                        Json.decodeFromJsonElement<Int>(recordCol)
                                    else
                                        0
                                } else if (type.equals("string")) {
                                    if (recordCol.jsonPrimitive.isString) {
                                        Json.decodeFromJsonElement<String>(recordCol)
                                    } else {
                                        String()
                                    }
                                } else if (type.equals("double")) {
                                    if (recordCol.jsonPrimitive.doubleOrNull != null)
                                        Json.decodeFromJsonElement<Double>(recordCol)
                                    else
                                        Double.NaN
                                } else if (type.equals("int64")) {
                                    if (recordCol.jsonPrimitive.longOrNull != null)
                                        Json.decodeFromJsonElement<Long>(recordCol)
                                    else
                                        0L
                                } else {
                                    String()
                                })

                                index++
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