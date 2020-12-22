package com.swnishan.materialdatetimepicker.timepicker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R


internal class TimePeriodViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(time: String) {
        (view as TextView).text = time
    }
}
