package ru.kima.moex.views.secdetails

import android.text.format.DateFormat
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import ru.kima.moex.views.MAGIC_DAYS
import ru.kima.moex.views.MILLISECONDS_IN_DAY
import java.util.Date


class LineChartXAxisValueFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {

        // Convert float value to date string
        // Convert from magic days to days and to milliseconds
        val emissionsMilliSince1970Time = ((value + MAGIC_DAYS) * MILLISECONDS_IN_DAY).toLong()

        // Show time in local version
        val timeMilliseconds = Date(emissionsMilliSince1970Time)

        return DateFormat.format("dd/MM/yy", timeMilliseconds).toString()
    }
}
