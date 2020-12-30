package com.swnishan.materialdatetimepicker.timepicker.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class LowFlingRecyclerView: RecyclerView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)


    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val velocityYNew=(velocityY*0.25).toInt()
        return super.fling(velocityX, velocityYNew)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        if(childCount>0){
            height=children.first().measuredHeight
            setPadding(paddingLeft, height, paddingRight, height)
            height= MeasureSpec.makeMeasureSpec(height*3, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, if(height>0)height else heightMeasureSpec)
    }
}