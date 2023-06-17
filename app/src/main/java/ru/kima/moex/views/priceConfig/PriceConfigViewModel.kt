package ru.kima.moex.views.priceConfig

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.moex.model.DatabaseSecurityService
import ru.kima.moex.model.SecurityEntity
import ru.kima.moex.views.Event

const val TAG = "PriceConfigViewModel"

class PriceConfigViewModel(
    private val database: DatabaseSecurityService
) : ViewModel() {
    private val _shouldReturn = MutableStateFlow<Event<Boolean?>>(Event(null))
    val shouldReturn = _shouldReturn.asStateFlow()
    private val _security = MutableStateFlow(SecurityEntity(0, "Empty", "Empty", 0.0, 0.0, false))
    var security = _security.asStateFlow()
    lateinit var secId: String


    fun updateSecId(sec: String) {
        secId = sec
        viewModelScope.launch(Dispatchers.IO) {
            val security = database.getSecurityBySecId(secId)
            security?.let {
                _security.value = it
            }
            if (security == null) {
                Log.d(TAG, "Security entity for $secId is null")
            }
        }
    }

    fun setTracked(flag: Boolean) {
        _security.value = _security.value.copy(is_tracked = flag)
    }

    fun updatePercent(pecent: Int) {
        _security.value = _security.value.copy(change_percent = pecent.toDouble())
    }

    fun onSavePressed() {
        viewModelScope.launch(Dispatchers.IO) {
            database.updateSecurity(_security.value)
            Log.d(TAG, "SAVED: ${_security.value}")
            _shouldReturn.value = Event(true)
        }
    }
}