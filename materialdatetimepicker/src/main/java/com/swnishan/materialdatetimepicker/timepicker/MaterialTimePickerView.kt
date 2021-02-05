package com.swnishan.materialdatetimepicker.timepicker

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.MaterialDateTimePickerException
import com.swnishan.materialdatetimepicker.common.SlowLinearLayoutManager
import com.swnishan.materialdatetimepicker.common.Utils
import com.swnishan.materialdatetimepicker.common.adapter.PickerAdapter
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView
import kotlinx.android.synthetic.main.view_time_picker.view.*
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime

class MaterialTimePickerView : BaseMaterialDateTimePickerView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        R.attr.materialTimePickerViewStyle
    )
    constructor(context: Context, attributeSet: AttributeSet?, defAttributeSet: Int) : super(
        context,
        attributeSet,
        defAttributeSet
    ) {
        setCustomAttributes(attributeSet, defAttributeSet, R.style.Widget_MaterialTimePicker)
        updateHoursAdapter()
        initTimeSelectionView()
        toggleTimePeriodView()
    }

    init {
        inflate(context, R.layout.view_time_picker, this)
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        setPadding(0, Utils.dimenToPx(context, R.dimen.date_picker_view_padding_top), 0, Utils.dimenToPx(context, R.dimen.date_picker_view_padding_bottom))
    }

    private fun setCustomAttributes(
        attributeSet: AttributeSet?,
        defAttributeSet: Int,
        defStyle: Int
    ) {
        context.obtainStyledAttributes(
            attributeSet,
            R.styleable.MaterialTimePickerView,
            defAttributeSet,
            defStyle
        ).apply {
            val highlighterColor = this.getColor(
                R.styleable.MaterialTimePickerView_highlighterColor,
                ContextCompat.getColor(context, R.color.O100)
            )

            val highlighterHeight = this.getDimension(
                R.styleable.MaterialTimePickerView_highlighterHeight,
                50f
            )

            val background = this.getDrawable(
                R.styleable.MaterialTimePickerView_android_background
            )

            textAppearance = this.getResourceId(
                R.styleable.MaterialTimePickerView_android_textAppearance,
                R.style.TextAppearance_MaterialTimePicker
            )
            hourAdapter.updateTextAppearance(textAppearance)
            minuteAdapter.updateTextAppearance(textAppearance)

            timeConvention = TimeConvention.values()[
                this.getInt(
                    R.styleable.MaterialTimePickerView_timeConvention,
                    0
                )
            ]

            fadeInDuration = this.getInt(
                R.styleable.MaterialTimePickerView_fadeInDuration,
                fadeInDuration.toInt()
            ).toLong()

            fadeOutDuration = this.getInt(
                R.styleable.MaterialTimePickerView_fadeOutDuration,
                fadeOutDuration.toInt()
            ).toLong()

            val fadeInAlpha = this.getFloat(
                R.styleable.MaterialTimePickerView_fadeInAlpha,
                fadeInAlpha
            )
            if (fadeInAlpha> 1f || fadeInAlpha <0f) throw MaterialDateTimePickerException("Given fadeIn alpha is invalid ($fadeInAlpha). fadeIn value should be 0 to 1")
            super.fadeInAlpha = fadeInAlpha

            val fadeOutAlpha = this.getFloat(
                R.styleable.MaterialTimePickerView_fadeOutAlpha,
                fadeOutAlpha
            )
            if (fadeOutAlpha> 1f || fadeInAlpha <0f) throw MaterialDateTimePickerException("Given fadeOut alpha is invalid ($fadeOutAlpha). fadeOut value should be 0 to 1")
            super.fadeOutAlpha = fadeOutAlpha

            val hour = this.getInt(R.styleable.MaterialTimePickerView_defaultHour, pickerTime.hour)
            if (hour> 23 || hour <0) throw MaterialDateTimePickerException("Given default hour is invalid. Hour should be in between 0 to 23")

            pickerTime = pickerTime.withHour(hour)
            timePeriod = if (pickerTime.hour >= 12) TimePeriod.PM else TimePeriod.AM

            val minute = this.getInt(R.styleable.MaterialTimePickerView_defaultMinute, pickerTime.minute)
            if (minute> 59 || hour <0) throw MaterialDateTimePickerException("Given default minute is invalid. Minute should be in between 0 to 59")

            pickerTime = pickerTime.withMinute(minute)

            viewCenter.setBackgroundColor(highlighterColor)
            viewCenter.layoutParams.height = highlighterHeight.toInt()
            TextViewCompat.setTextAppearance(tvHourTimeSeparator, textAppearance)

            if (background is ColorDrawable) {
                val backgroundColor = background.color
                viewTopShadeHour.setBackgroundColor(backgroundColor)
                viewTopShadeMinute.setBackgroundColor(backgroundColor)
                viewTopShadeTimePeriod.setBackgroundColor(backgroundColor)
                viewBottomShadeHour.setBackgroundColor(backgroundColor)
                viewBottomShadeMinute.setBackgroundColor(backgroundColor)
                viewBottomShadeTimePeriod.setBackgroundColor(backgroundColor)
                setBackgroundColor(backgroundColor)
            }
        }.recycle()
    }

    private var textAppearance: Int = R.style.TextAppearance_MaterialTimePicker
    private val hours24 = (0..23).mapIndexed { index, value -> TimeModel.Hour(index, String.format("%02d", value), value) }
    private val hours12 = (1..12).mapIndexed { index, value -> TimeModel.Hour(index, String.format("%02d", value), value) }
    private val minutes = (0..59).mapIndexed { index, value -> TimeModel.Minute(index, String.format("%02d", value), value) }

    private val hourSnapHelper = LinearSnapHelper()
    private val minuteSnapHelper = LinearSnapHelper()
    private val timePeriodSnapHelper = LinearSnapHelper()

    private val hourAdapter = PickerAdapter(hours24, textAppearance) { position -> onItemClicked(position, rvHours) }
    private val minuteAdapter = PickerAdapter(minutes, textAppearance) { position -> onItemClicked(position, rvMinute) }

    private var pickerTime: LocalTime = LocalTime.now()
    private var onTimePickedListener: OnTimePickedListener? = null
    private var timeConvention: TimeConvention = TimeConvention.HOURS_24
    private var timePeriod: TimePeriod = if (pickerTime.hour >= 12) TimePeriod.PM else TimePeriod.AM

    internal fun onTimePicked() {
        onTimePickedListener?.onTimePicked(getTime())
    }

    /**
     * Get selected hour
     *
     * @return
     */
    fun getHour(): Int {
        val position = hourSnapHelper.getSnapPosition(rvHours)
        return when (timeConvention) {
            TimeConvention.HOURS_24 -> position % hours24.size
            TimeConvention.HOURS_12 -> {
                return (hourAdapter.getModelAtPosition(position) as TimeModel.Hour).hour % 12 + if (getTimePeriod() == TimePeriod.PM) 12 else 0
            }
        }
    }

    /**
     * Get selected minute
     *
     * @return
     */
    fun getMinute(): Int = minuteSnapHelper.getSnapPosition(rvMinute) % minutes.size

    /**
     * Get selected time period
     *
     * @return
     */
    fun getTimePeriod(): TimePeriod = TimePeriod.values()[timePeriodSnapHelper.getSnapPosition(rvTimePeriod)]

    /**
     * get selected time as Long value
     *
     * @return
     */
    fun getTime() = OffsetDateTime.now().withHour(pickerTime.hour).withMinute(pickerTime.minute).withSecond(0)
        .withNano(0).toInstant().toEpochMilli()

    private fun updateHoursAdapter() {
        val hours = when (timeConvention) {
            TimeConvention.HOURS_24 -> hours24
            TimeConvention.HOURS_12 -> hours12
        }
        hourAdapter.updateItems(hours)
    }

    private fun toggleTimePeriodView() {
        rvTimePeriod.isVisible = timeConvention == TimeConvention.HOURS_12
    }

    private fun initTimeSelectionView() {
        rvHours.apply {
            setHasFixedSize(true)
            adapter = hourAdapter
            layoutManager = SlowLinearLayoutManager(context, rvHours) { scrollToHour() }
            hourSnapHelper.attachToRecyclerView(this)
            addListeners { viewId -> updateTimeWhenScroll(viewId) }
        }

        rvMinute.apply {
            setHasFixedSize(true)
            adapter = minuteAdapter
            layoutManager = SlowLinearLayoutManager(context, rvMinute) { scrollToMinute() }
            minuteSnapHelper.attachToRecyclerView(this)
            addListeners { viewId -> updateTimeWhenScroll(viewId) }
        }

        rvTimePeriod.apply {
            setHasFixedSize(true)
            adapter = PickerAdapter(
                listOf(TimePeriod.AM, TimePeriod.PM).mapIndexed { index, timePeriod -> TimeModel.TimePeriod(index, timePeriod.name, timePeriod.ordinal) },
                textAppearance,
                PickerAdapter.ScrollOptions.SCROLL_ITEM_LIMIT
            ) { position -> onItemClicked(position, rvTimePeriod) }
            layoutManager = SlowLinearLayoutManager(context, rvTimePeriod) { scrollToTimePeriod() }
            timePeriodSnapHelper.attachToRecyclerView(this)
            addListeners { viewId -> updateTimeWhenScroll(viewId) }
        }
    }

    override fun fadeView(view: RecyclerView, duration: Long, alpha: Float) {
        when (view.id) {
            R.id.rvHours -> super.startFadeAnimation(listOf(viewTopShadeHour, viewBottomShadeHour), duration, alpha)
            R.id.rvMinute -> super.startFadeAnimation(listOf(viewTopShadeMinute, viewBottomShadeMinute), duration, alpha)
            R.id.rvTimePeriod -> super.startFadeAnimation(listOf(viewTopShadeTimePeriod, viewBottomShadeTimePeriod), duration, alpha)
        }
    }

    private fun updateTimeWhenScroll(viewId: Int) {
        when (viewId) {
            R.id.rvHours -> pickerTime = pickerTime.withHour(getHour())
            R.id.rvMinute -> pickerTime = pickerTime.withMinute(getMinute())
            R.id.rvTimePeriod -> {
                timePeriod = getTimePeriod()
                pickerTime = pickerTime.withHour(getHour())
            }
        }
    }

    private fun scrollToTime() {
        scrollToHour()
        scrollToMinute()
        scrollToTimePeriod()
    }

    private fun scrollToHour() {
        val scrollPosition = getScrollPosition(hourAdapter, getHoursBasedOnClockType(), getHourModel(pickerTime.hour))
        rvHours.scrollToPosition(scrollPosition)
    }

    private fun scrollToMinute() {
        rvMinute.scrollToPosition(getScrollPosition(minuteAdapter, minutes, getMinuteModel(pickerTime.minute)))
    }

    private fun scrollToTimePeriod() {
        val position = if (timePeriod == TimePeriod.PM) 1 else 0
        rvTimePeriod.scrollToPosition(position)
    }

    private fun getHoursBasedOnClockType(): List<TimeModel.Hour> {
        return when (timeConvention) {
            TimeConvention.HOURS_24 -> hours24
            TimeConvention.HOURS_12 -> hours12
        }
    }

    private fun getHourModel(hour: Int) = when (timeConvention) {
        TimeConvention.HOURS_24 -> hours24.firstOrNull { it.hour == hour % 24 }
            ?: throw MaterialDateTimePickerException("Cannot find given Hour in given 24 hours range (size: ${hours24.size} index: $hour)")
        TimeConvention.HOURS_12 -> hours12.firstOrNull { it.hour == if (hour % 12 == 0) 12 else hour % 12 }
            ?: throw MaterialDateTimePickerException("Cannot find given Hour in given 12 hours range (size: ${hours12.size} index: $hour)")
    }

    private fun getMinuteModel(minute: Int) = minutes.firstOrNull { it.minute == minute }
        ?: throw MaterialDateTimePickerException("Cannot find given Minute in given minutes range (size: ${minutes.size} index: $minute)")

    fun setOnTimePickedListener(onTimePickedListener: OnTimePickedListener?) {
        this.onTimePickedListener = onTimePickedListener
    }

    /**
     * Set selected hour. The range should be 0 to 23
     * The time period will be updated based on the set [hour].
     * Ex: If current selected time period is [TimePeriod.AM] and set hour is 14
     * the time period will be updated to [TimePeriod.PM]
     *
     * @param hour
     */
    fun setHour(@IntRange(from = 0, to = 23)hour: Int) {
        setTimePeriod(if (hour >= 12)TimePeriod.PM else TimePeriod.AM)
        val setHour = when (timeConvention) {
            TimeConvention.HOURS_24 -> hour
            TimeConvention.HOURS_12 -> hour % 12 + if (timePeriod == TimePeriod.PM) 12 else 0
        }
        pickerTime = pickerTime.withHour(setHour)
        scrollToHour()
    }

    /**
     * Set selected minute. The range should be 0 to 60
     *
     * @param minute
     */
    fun setMinute(@IntRange(from = 0, to = 60) minute: Int) {
        pickerTime = pickerTime.withMinute(minute)
        scrollToMinute()
    }

    /**
     * Set selected time period [TimePeriod.AM] or [TimePeriod.PM]
     * The selected hour will be updated base on set [timePeriod].
     * Ex: If current hour is 01:00 and set time period is [TimePeriod.PM],
     * the hour will be updated to 13:00
     *
     * @param timePeriod
     */
    fun setTimePeriod(timePeriod: TimePeriod) {
        if (timePeriod == this.timePeriod) return
        this.timePeriod = timePeriod
        pickerTime = when {
            pickerTime.hour < 12 && timePeriod == TimePeriod.PM -> pickerTime.plusHours(12)
            pickerTime.hour >= 12 && timePeriod == TimePeriod.AM -> pickerTime.minusHours(12)
            else -> pickerTime
        }
        scrollToTimePeriod()
    }

    /**
     * Set preferred time conversion to be displayed
     * [TimeConvention.HOURS_12] or [TimeConvention.HOURS_24]
     *
     * @param timeConvention
     */
    fun setTimeConvention(timeConvention: TimeConvention) {
        this.timeConvention = timeConvention
        updateHoursAdapter()
        toggleTimePeriodView()
        scrollToTime()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState: Parcelable? = super.onSaveInstanceState()
        superState?.let {
            val state = SavedState(superState)
            state.selectedHour = pickerTime.hour
            state.selectedMinute = pickerTime.minute
            state.selectedTimePeriod = timePeriod.name
            return state
        } ?: run {
            return superState
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                pickerTime = pickerTime.withHour(state.selectedHour).withMinute(state.selectedMinute)
                timePeriod = TimePeriod.valueOf(state.selectedTimePeriod)
            }
            else -> {
                super.onRestoreInstanceState(state)
            }
        }
    }

    internal class SavedState : BaseSavedState {
        var selectedHour: Int = OffsetDateTime.now().hour
        var selectedMinute: Int = OffsetDateTime.now().minute
        var selectedTimePeriod: String = TimePeriod.AM.name

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            selectedHour = source.readInt()
            selectedHour = source.readInt()
            selectedTimePeriod = source.readString() ?: TimePeriod.AM.name
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(selectedHour)
            out.writeInt(selectedMinute)
            out.writeString(selectedTimePeriod)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState> {
                    return newArray(size)
                }
            }
        }
    }

    fun interface OnTimePickedListener {
        fun onTimePicked(time: Long)
    }

    enum class TimeConvention {
        HOURS_24, HOURS_12
    }

    enum class TimePeriod {
        AM, PM
    }
}
