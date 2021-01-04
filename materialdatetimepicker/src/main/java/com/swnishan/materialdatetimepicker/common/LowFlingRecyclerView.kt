package com.swnishan.materialdatetimepicker.common

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class LowFlingRecyclerView: RecyclerView {

    private var onHeightUpdateListener: OnItemHeightUpdateListener?=null
    var heightR:Int=0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)


    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val velocityYNew=(velocityY*0.25).toInt()
        return super.fling(velocityX, velocityYNew)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measuredItemHeight = 0
        if(childCount>0){
            measuredItemHeight=children.first().measuredHeight
            heightR=measuredItemHeight
            if(measuredItemHeight>0)onHeightUpdateListener?.onUpdate(measuredItemHeight)
            setPadding(paddingLeft, measuredItemHeight, paddingRight, measuredItemHeight)
            measuredItemHeight = MeasureSpec.makeMeasureSpec(measuredItemHeight*3, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, if(measuredItemHeight>0)measuredItemHeight else heightMeasureSpec)
    }

    internal interface OnItemHeightUpdateListener{
        fun onUpdate(measuredHeight:Int)
    }
}