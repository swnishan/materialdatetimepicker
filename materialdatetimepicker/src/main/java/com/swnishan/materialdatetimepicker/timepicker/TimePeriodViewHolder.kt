package com.swnishan.materialdatetimepicker.timepicker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R


internal class TimePeriodViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(time: String, isSelected: Boolean) {
        (view as TextView).text = time

        val color = if (isSelected) R.color.black else R.color.O700
        view.setTextColor(view.context.resources.getColor(color))
    }
}
