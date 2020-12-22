package com.swnishan.materialdatetimepicker.timepicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R

internal class TimePeriodAdapter(private val items: List<String>) : RecyclerView.Adapter<TimePeriodViewHolder>() {

    private var selectedTime: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimePeriodViewHolder {
        return TimePeriodViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_time_picker, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TimePeriodViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun getSelectedTime() = selectedTime
}
