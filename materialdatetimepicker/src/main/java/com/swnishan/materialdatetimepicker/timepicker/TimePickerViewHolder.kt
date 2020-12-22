package com.swnishan.materialdatetimepicker.timepicker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R


internal class TimePickerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(time: Int) {
        (view as TextView).text = String.format("%02d", time)
    }
}
