package com.swnishan.materialdatetimepicker.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.PickerModel

internal class PickerAdapter(private var items: List<PickerModel>, private val textAppearance: Int, private val scrollOption: ScrollOptions=ScrollOptions.SCROLL_INT_MAX) : RecyclerView.Adapter<PickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerViewHolder {
        return PickerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picker, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PickerViewHolder, position: Int) {
        // Here get actual element from the items by position%items.size
        holder.bind(items[position % items.size], textAppearance)
    }

    /**
     * To make recycler view repeatable here set the item count as Int.MAX_VALUE.
     * So, it seems there's lots of items but the actual item count is items.size
     */
    override fun getItemCount(): Int = if(scrollOption==ScrollOptions.SCROLL_INT_MAX) Int.MAX_VALUE else items.size

    fun setTimes(items: List<PickerModel>){
        this.items = items
        notifyDataSetChanged()
    }

    enum class ScrollOptions{
        SCROLL_INT_MAX, SCROLL_ITEM_LIMIT
    }
}
