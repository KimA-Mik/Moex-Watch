package ru.kima.moex.views.secdetails

import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.moex.model.SecurityDayPrice
import ru.kima.moex.model.SecurityService
import ru.kima.moex.views.MAGIC_DAYS
import ru.kima.moex.views.MILLISECONDS_IN_DAY
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
    private val _candleData = MutableStateFlow(CandleData())
    val candleData = _candleData.asStateFlow()

    @ColorInt
    var colorGreen = 0
    @ColorInt
    var colorRed = 0
    private fun loadData() = viewModelScope.launch(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -DAYS_IN_YEAR)
        val date = calendar.time

        _priceData.value = emptyList()
        val response = securityService.getSecurityPriceHistoryFrom(SecurityId, date)
        response.collect { responseList ->
            _priceData.value = listOf(_priceData.value, responseList).flatten()
            updateCandleData(_priceData.value)
        }
    }

    private fun updateCandleData(priceData: List<SecurityDayPrice>) {
        val values = arrayListOf<CandleEntry>()
        for (entry in priceData) {
            // TODO:decide that to do with NaN values
            if (entry.highPrice.isNaN()
                || entry.lowPrice.isNaN()
                || entry.closePrice.isNaN()
                || entry.openPrice.isNaN()
            ) {
                continue
            }
            /* Plot library uses timestamps as x value
            * unfortunately i't cant handles big values
            * so i convert UNIX milliseconds to days
            * and subtract magic number of days to keep index small.
            * Thanks to https://stackoverflow.com/questions/53128197/mpandroidchart-combinedchart-candlestick-real-body-not-showing
            * for pointing to this behaviour
            */
            val daysSinceEpoch = entry.date.time / MILLISECONDS_IN_DAY - MAGIC_DAYS
            val value = CandleEntry(
                daysSinceEpoch,
                entry.highPrice.toFloat(),
                entry.lowPrice.toFloat(),
                entry.openPrice.toFloat(),
                entry.closePrice.toFloat()
            )
            values += value
        }
        val set = CandleDataSet(values, SecurityId)
        set.setDrawIcons(false)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = Color.rgb(80, 80, 80)
        set.shadowColor = Color.DKGRAY
        set.shadowWidth = 1f
        set.decreasingColor = Color.RED
        set.decreasingPaintStyle = Paint.Style.FILL
        set.increasingColor = Color.rgb(122, 242, 84)
        set.increasingPaintStyle = Paint.Style.FILL_AND_STROKE
        set.neutralColor = Color.BLUE

        val data = CandleData(set)
        _candleData.value = data
    }

    companion object {
        private const val DAYS_IN_YEAR = 365

    }
}