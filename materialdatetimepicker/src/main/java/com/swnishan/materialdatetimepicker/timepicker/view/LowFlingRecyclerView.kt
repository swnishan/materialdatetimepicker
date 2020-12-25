package com.swnishan.materialdatetimepicker.timepicker.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class LowFlingRecyclerView: RecyclerView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)


    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val velocityYNew=(velocityY*0.25).toInt()
        return super.fling(velocityX, velocityYNew)
    }
}