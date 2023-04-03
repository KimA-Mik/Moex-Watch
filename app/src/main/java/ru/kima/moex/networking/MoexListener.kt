package ru.kima.moex.networking


fun interface MoexListener<T> {
    fun getResult(result: T)
}
