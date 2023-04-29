package ru.kima.moex.views.seclist

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/*
 * Thanks to
 * https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
 */
const val MARGIN = 16

class DividerItemDecoration : ItemDecoration {
    private var divider: Drawable

    /**
     * Default divider will be used
     */
    constructor(context: Context) {
        val styledAttributes: TypedArray = context.obtainStyledAttributes(ATTRS)
        divider = styledAttributes.getDrawable(0)!!
        styledAttributes.recycle()
    }

    /**
     * Custom divider will be used
     */
    constructor(context: Context?, resId: Int) {
        divider = context?.let { ContextCompat.getDrawable(it, resId) }!!
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft + MARGIN
        val right = parent.width - parent.paddingRight - MARGIN
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin //+ MARGIN
            val bottom = top + divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }


    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}
