package ru.kima.moex.ui.seclist

import androidx.lifecycle.ViewModel
import ru.kima.moex.data.Security

class SeclistViewModel : ViewModel() {
    val securities = mutableListOf<Security>()
}