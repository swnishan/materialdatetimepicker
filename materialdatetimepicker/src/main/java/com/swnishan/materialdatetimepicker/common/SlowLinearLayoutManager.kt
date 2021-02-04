package com.swnishan.materialdatetimepicker.common

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class SlowLinearLayoutManager : LinearLayoutManager {

    private var recyclerView: RecyclerView? = null

    constructor(context: Context?, recyclerView: RecyclerView) : this(context) {
        this.recyclerView = recyclerView
    }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                }
            }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    override fun setMeasuredDimension(childrenBounds: Rect?, wSpec: Int, hSpec: Int) {
        var measuredItemHeight = 0
        if (childCount > 0) {
            measuredItemHeight = getChildAt(0)?.measuredHeight ?: return
            recyclerView?.setPadding(paddingLeft, measuredItemHeight, paddingRight, measuredItemHeight)
            measuredItemHeight = View.MeasureSpec.makeMeasureSpec(measuredItemHeight * 3, View.MeasureSpec.EXACTLY)
        }
        super.setMeasuredDimension(childrenBounds, wSpec, if (measuredItemHeight> 0) measuredItemHeight else hSpec)
    }

    companion object {
        private const val MILLISECONDS_PER_INCH = 400f
    }
}
