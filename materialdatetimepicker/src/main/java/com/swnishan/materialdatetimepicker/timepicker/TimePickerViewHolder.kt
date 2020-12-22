package com.swnishan.materialdatetimepicker.timepicker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R


internal class TimePickerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(time: Int, isSelected: Boolean) {
        (view as TextView).text = String.format("%02d", time)

        val color = if (isSelected) R.color.black else R.color.O700
        view.setTextColor(view.context.resources.getColor(color))
    }
}
