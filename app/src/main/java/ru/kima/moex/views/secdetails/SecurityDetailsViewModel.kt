package ru.kima.moex.views.secdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.moex.model.SecurityDayPrice
import ru.kima.moex.model.SecurityService
import java.util.Calendar

class SecurityDetailsViewModel(
    private val securityService: SecurityService
) : ViewModel() {
    var SecurityId: String = String()
        set(value) {
            field = value
            loadData()
        }
    private val _priceData = MutableStateFlow<List<SecurityDayPrice>>(emptyList())
    val priceData = _priceData.asStateFlow()
    private fun loadData() = viewModelScope.launch(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -DAYS_IN_YEAR)
        val date = calendar.time

        _priceData.value = emptyList()
        val response = securityService.getSecurityPriceHistoryFrom(SecurityId, date)
        response.collect { responseList ->
            _priceData.value = listOf(_priceData.value, responseList).flatten()
        }
    }

    companion object {
        private const val DAYS_IN_YEAR = 365
    }
}