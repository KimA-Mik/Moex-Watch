package ru.kima.moex.views.seclist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.moex.model.DatabaseSecurityService
import ru.kima.moex.model.Security
import ru.kima.moex.model.SecurityService
import ru.kima.moex.views.Event

class SeclistViewModel(
    private val securityService: SecurityService,
    private val database: DatabaseSecurityService
) : ViewModel(), SecurityActionListener, SeclistMenuListener {
    private var tempData = emptyList<Security>()
    private val _data = MutableStateFlow<List<Security>>(emptyList())
    val data = _data.asStateFlow()

    private val _showDetails = MutableStateFlow<Event<Security?>>(Event(null))
    val showDetails = _showDetails.asStateFlow()

    private val _showFavorite = MutableStateFlow<Boolean>(false)
    val showFavorite = _showFavorite.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch(Dispatchers.IO) {
        _data.value = securityService.fetchSecurities()
        tempData = _data.value
    }

    override fun onSecurityDetail(security: Security) {
        _showDetails.value = Event(security)
    }

    override fun onQueryString(query: String?) {
        _showFavorite.value = false
        if (query.isNullOrBlank()) {
            _data.value = tempData
            return
        }
        _data.value =
            tempData.filter { it.SECID.contains(query, true) || it.SECNAME.contains(query, true) }
    }

    override fun onFavoriteChange() {
        viewModelScope.launch {
            if (_showFavorite.value) {
                _showFavorite.value = false
                _data.value = tempData
            } else {
                database.getAllFavorites().collect { favorites ->
                    _showFavorite.value = true
                    _data.value =
                        tempData.filter { (SECID) ->
                            favorites.any { entity -> entity.SECID == SECID }
                        }
                }
            }
        }
    }
}