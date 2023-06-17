package ru.kima.moex

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme


class ResourceProvider private constructor(context: Context) {

    fun getColor(colorId: Int): Int {
        return resources.getColor(colorId, theme)
    }

    fun getColorFromAttr(attrId: Int): Int {
//        val typedValue = TypedValue()
//        resources.getValue(attrId, typedValue, true)
//        var color = 0
//        color = resources.getColor(typedValue.resourceId, theme)
//        return color
        val a = theme.obtainStyledAttributes(
            R.style.Theme_MoexWatch,
            intArrayOf(attrId)
        )
        val intColor = a.getColor(0, 0)
        a.recycle()
        return intColor
    }

    fun getString(stringId: Int): String {
        return resources.getString(stringId)
    }

    private var theme: Theme
    private val resources: Resources

    init {
        resources = context.resources
        theme = context.theme
    }


    companion object {

        private var provider: ResourceProvider? = null

        fun initialize(context: Context) {
            provider = ResourceProvider(context)
            context.theme
        }

        fun getInstance(): ResourceProvider {
            if (provider == null) {
                throw Exception("Uninitialized resource provider usage")
            }
            return provider as ResourceProvider
        }

        fun updateTheme(theme: Theme) {
            provider?.let {
                it.theme = theme
            }
        }

        fun updateTheme(context: Context) {
            provider?.let {
                it.theme = context.theme
            }
        }
    }
}