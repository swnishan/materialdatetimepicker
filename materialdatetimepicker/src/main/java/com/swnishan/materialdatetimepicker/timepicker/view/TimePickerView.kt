package com.swnishan.materialdatetimepicker.timepicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.timepicker.TimePickerAdapter
import kotlinx.android.synthetic.main.view_time_picker.view.*
import org.threeten.bp.OffsetDateTime
import kotlin.math.absoluteValue

class TimePickerView(context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attributes, defStyleAttr) {

    private val hours = (0..23).toList()
    private val minute = (0..59).toList()
    private val hourAdapter = TimePickerAdapter(hours)
    private val minuteAdapter = TimePickerAdapter(minute)
    private val hourSnapHelper = LinearSnapHelper()
    private val minuteSnapHelper = LinearSnapHelper()

    private var pickerTime: OffsetDateTime = OffsetDateTime.now()
    private var onTimePickedListener: OnTimePickedListener? = null

    init {
        inflate(
            ContextThemeWrapper(context,R.style.TimePickerStyle),
            R.layout.view_time_picker,
            this
        )
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        initTimeSelectionView()
        scrollToTime()
    }

    internal fun onTimePicked() {
        pickerTime = pickerTime.withHour(hourAdapter.getSelectedTime() % hours.size)
        pickerTime = pickerTime.withMinute(minuteAdapter.getSelectedTime() % minute.size)
        onTimePickedListener?.onTimePicked(pickerTime)
    }

    private fun initTimeSelectionView() {
        rvHours.apply {
            setHasFixedSize(true)
            adapter = hourAdapter
            layoutManager = LinearLayoutManager(context)
            hourSnapHelper.attachToRecyclerView(this)
        }

        rvMinute.apply {
            setHasFixedSize(true)
            adapter = minuteAdapter
            layoutManager = LinearLayoutManager(context)
            minuteSnapHelper.attachToRecyclerView(this)
        }

        rvHours.setOnScrollListener(hourSnapHelper, hourAdapter)
        rvMinute.setOnScrollListener(minuteSnapHelper, minuteAdapter)
    }

    private fun scrollToTime() {
        rvHours.scrollToPosition(getScrollPosition(hours.size, pickerTime.hour))
        rvHours.smoothScrollBy(0, 1)
        rvMinute.scrollToPosition(getScrollPosition(minute.size, pickerTime.minute))
        rvMinute.smoothScrollBy(0, 1)
    }

    /**
     * @see TimePickerAdapter
     * Here we get the scroll position with relative to middle position of list of items
     * since we set the adapter count as Int.MAX_VALUE
     * Then user can scroll to both ways like repeatable list.
     */
    private fun getScrollPosition(listSize: Int, time: Int): Int {
        var scrollPosition = Int.MAX_VALUE / 2
        val position = scrollPosition % listSize

        val diff = (time - position).absoluteValue
        if (time > position) scrollPosition += diff else scrollPosition -= diff

        return scrollPosition
    }

    fun setOnTimePickedListener(onTimePickedListener: OnTimePickedListener?) {
        this.onTimePickedListener = onTimePickedListener
    }

    fun setTime(offsetDateTime: OffsetDateTime) {
        this.pickerTime = offsetDateTime
        scrollToTime()
    }

    private fun RecyclerView.setOnScrollListener(
        snapHelper: LinearSnapHelper,
        adapter: TimePickerAdapter
    ) {
        this.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val view = snapHelper.findSnapView(recyclerView.layoutManager) ?: return
                val position = recyclerView.layoutManager?.getPosition(view)
                adapter.setSelectedTime(position ?: -0)
            }
        })
    }

    interface OnTimePickedListener {
        fun onTimePicked(time: OffsetDateTime)
    }

}