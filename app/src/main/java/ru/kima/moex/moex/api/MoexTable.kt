package ru.kima.moex.moex.api

class MoexTable {
    var name = String()
    var data : MutableList<MutableMap<String, Any>> = mutableListOf<MutableMap<String, Any>>()
    var fields : MutableList<String> = mutableListOf()
}