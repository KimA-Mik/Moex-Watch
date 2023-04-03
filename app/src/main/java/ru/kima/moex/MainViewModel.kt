package ru.kima.moex

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TEXT_KEY = "TEXT"

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var text: String
        get() = savedStateHandle[TEXT_KEY] ?: ""
        set(value) = savedStateHandle.set(TEXT_KEY, value)
}