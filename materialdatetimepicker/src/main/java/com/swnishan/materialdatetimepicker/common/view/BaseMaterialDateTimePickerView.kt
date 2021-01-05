package com.swnishan.materialdatetimepicker.common.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.common.PickerModel
import com.swnishan.materialdatetimepicker.common.adapter.PickerAdapter
import kotlin.math.absoluteValue

open class BaseMaterialDateTimePickerView:ConstraintLayout{
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defAttributeSet: Int) : super(context, attributeSet, defAttributeSet)

    /**
     * Here we get the scroll position with relative to middle position of list of items
     * since we set the adapter count as Int.MAX_VALUE
     */
    internal fun getScrollPosition(adapter: PickerAdapter, list: List<PickerModel>, model: PickerModel): Int {
        var scrollPosition = adapter.itemCount/2
        val position = scrollPosition % list.size

        val diff = (model.index - position).absoluteValue
        if (model.index > position) scrollPosition += diff else scrollPosition -= diff

        return scrollPosition
    }

    internal fun animateShadeView(views: List<View>, duration: Long, alpha: Float) {
        views.forEach {
            it.animate().alpha(alpha).setDuration(duration)
                .withEndAction { it.alpha = alpha }.start()
        }
    }

    internal open fun onItemClicked(position: Int, rv: RecyclerView){
        rv.smoothScrollToPosition(position)
    }

    internal fun LinearSnapHelper.getSnapPosition(rv:RecyclerView): Int {
        val view=findSnapView(rv.layoutManager)?:return 0
        return rv.getChildAdapterPosition(view)
    }
}