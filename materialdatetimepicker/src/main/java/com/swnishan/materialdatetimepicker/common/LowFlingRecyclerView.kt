package com.swnishan.materialdatetimepicker.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

internal class LowFlingRecyclerView: RecyclerView {

    private var isSetFixedHeight=false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)


    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val velocityYNew=(velocityY*0.25).toInt()
        return super.fling(velocityX, velocityYNew)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(childCount>0 && !isSetFixedHeight){
            isSetFixedHeight=true
            val measuredItemHeight=children.first().height
            setPadding(paddingLeft, measuredItemHeight, paddingRight, measuredItemHeight)
            measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredItemHeight*3, MeasureSpec.EXACTLY))
        }
    }

}