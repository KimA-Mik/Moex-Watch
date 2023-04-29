package ru.kima.moex.views.seclist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.moex.model.Security
import ru.kima.moex.model.SecurityService
import ru.kima.moex.views.Event

class SeclistViewModel(
    private val securityService: SecurityService
) : ViewModel(), SecurityActionListener {
    private val _data = MutableStateFlow<List<Security>>(emptyList())
    val data = _data.asStateFlow()

    private val _showDetails = MutableStateFlow<Event<Security?>>(Event(null))
    val showDetails = _showDetails.asStateFlow()

    init {
        loadData()
    }

    fun loadData() = viewModelScope.launch(Dispatchers.IO) {
        _data.value = securityService.fetchSecurities()
    }

    override fun onSecurityDetail(security: Security) {
        _showDetails.value = Event(security)
    }
}