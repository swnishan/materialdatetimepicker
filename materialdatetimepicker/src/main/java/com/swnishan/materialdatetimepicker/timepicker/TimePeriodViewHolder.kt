package com.swnishan.materialdatetimepicker.timepicker

import android.view.View
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView


internal class TimePeriodViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(time: String, textAppearance: Int) {
        (view as TextView).text = time
        TextViewCompat.setTextAppearance(view, textAppearance)
    }
}
