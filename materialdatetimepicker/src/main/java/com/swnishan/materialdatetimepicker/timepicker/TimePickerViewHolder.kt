package com.swnishan.materialdatetimepicker.timepicker

import android.view.View
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView


internal class TimePickerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(time: Int, textAppearance: Int) {
        (view as TextView).text = String.format("%02d", time)
        TextViewCompat.setTextAppearance(view, textAppearance)
    }
}
