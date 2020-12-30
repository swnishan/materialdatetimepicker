package com.swnishan.materialdatetimepicker.timepicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R

internal class TimePickerAdapter(private var items: List<Int>, private val textAppearance: Int) : RecyclerView.Adapter<TimePickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimePickerViewHolder {
        return TimePickerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_time_picker, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TimePickerViewHolder, position: Int) {
        // Here get actual element from the items by position%items.size
        holder.bind(items[position % items.size], textAppearance)
    }

    /**
     * To make recycler view repeatable here set the item count as Int.MAX_VALUE.
     * So, it seems there's lots of items but the actual item count is items.size
     */
    override fun getItemCount(): Int = Int.MAX_VALUE

    fun setTimes(items: List<Int>){
        this.items = items
        notifyDataSetChanged()
    }
}
