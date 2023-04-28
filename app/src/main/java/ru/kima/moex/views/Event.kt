package ru.kima.moex.views

class Event<T>(
    private val value: T
) {
    private var isHandled = false

    fun getValue(): T? {
        if (isHandled)
            return null
        isHandled = true
        return value
    }
}