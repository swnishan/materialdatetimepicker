package com.swnishan.materialdatetimepicker.datepicker

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.MaterialDateTimePickerException
import com.swnishan.materialdatetimepicker.common.SlowLinearLayoutManager
import com.swnishan.materialdatetimepicker.common.Utils
import com.swnishan.materialdatetimepicker.common.adapter.PickerAdapter
import com.swnishan.materialdatetimepicker.common.toLocalDate
import com.swnishan.materialdatetimepicker.common.toLong
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView
import com.swnishan.materialdatetimepicker.timepicker.MaterialTimePickerView
import kotlinx.android.synthetic.main.view_date_picker.view.*
import kotlinx.android.synthetic.main.view_date_picker.view.viewCenter
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.TextStyle
import java.util.Locale

class MaterialDatePickerView : BaseMaterialDateTimePickerView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        R.attr.materialDatePickerViewStyle
    )
    constructor(context: Context, attributeSet: AttributeSet?, defAttributeSet: Int) : super(
        context,
        attributeSet,
        defAttributeSet
    ) {
        setCustomAttributes(attributeSet, defAttributeSet, R.style.Widget_MaterialDatePicker)
        initDateSelectionView()
        updateYearsAdapter()
        updateMonthsAdapter()
        updateDaysAdapter()
    }

    init {
        inflate(context, R.layout.view_date_picker, this)
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        setPadding(0, Utils.dimenToPx(context, R.dimen.time_picker_view_padding_top), 0, Utils.dimenToPx(context, R.dimen.time_picker_view_padding_bottom))
    }

    private fun setCustomAttributes(
        attributeSet: AttributeSet?,
        defAttributeSet: Int,
        defStyle: Int
    ) {
        context.obtainStyledAttributes(
            attributeSet,
            R.styleable.MaterialDatePickerView,
            defAttributeSet,
            defStyle
        ).apply {
            val highlighterColor = this.getColor(
                R.styleable.MaterialDatePickerView_highlighterColor,
                ContextCompat.getColor(context, R.color.O100)
            )

            val highlighterHeight = this.getDimension(
                R.styleable.MaterialDatePickerView_highlighterHeight,
                50f
            )

            val background = this.getDrawable(
                R.styleable.MaterialTimePickerView_android_background
            )

            textAppearance = this.getResourceId(
                R.styleable.MaterialDatePickerView_android_textAppearance,
                R.style.TextAppearance_MaterialTimePicker
            )
            yearAdapter.updateTextAppearance(textAppearance)
            monthAdapter.updateTextAppearance(textAppearance)
            dayAdapter.updateTextAppearance(textAppearance)

            dateFormat = DateFormat.values()[
                this.getInt(
                    R.styleable.MaterialDatePickerView_dateFormat,
                    0
                )
            ]

            fadeInDuration = this.getInt(
                R.styleable.MaterialDatePickerView_fadeInDuration,
                fadeInDuration.toInt()
            ).toLong()

            fadeOutDuration = this.getInt(
                R.styleable.MaterialDatePickerView_fadeOutDuration,
                fadeOutDuration.toInt()
            ).toLong()

            val fadeInAlpha = this.getFloat(
                R.styleable.MaterialDatePickerView_fadeInAlpha,
                fadeInAlpha
            )
            if (fadeInAlpha> 1f || fadeInAlpha <0f) throw MaterialDateTimePickerException("Given fadeIn alpha is invalid ($fadeInAlpha). fadeIn value should be 0 to 1")
            super.fadeInAlpha = fadeInAlpha

            val fadeOutAlpha = this.getFloat(
                R.styleable.MaterialDatePickerView_fadeOutAlpha,
                fadeOutAlpha
            )
            if (fadeOutAlpha> 1f || fadeInAlpha <0f) throw MaterialDateTimePickerException("Given fadeOut alpha is invalid ($fadeOutAlpha). fadeOut value should be 0 to 1")
            super.fadeOutAlpha = fadeOutAlpha

            val minYear = this.getInt(R.styleable.MaterialDatePickerView_minYear, 1950)
            val maxYear = this.getInt(R.styleable.MaterialDatePickerView_maxYear, 2100)
            yearsRange = (minYear..maxYear)

            val year = this.getInt(R.styleable.MaterialDatePickerView_defaultYear, pickerDate.year)
            if (year <minYear || year> maxYear) throw MaterialDateTimePickerException("Default year ($year) out of the year range. It should be in between $minYear to $maxYear")

            pickerDate = pickerDate.withYear(year)
            pickerDate = pickerDate.withMonth(this.getInt(R.styleable.MaterialDatePickerView_defaultMonth, pickerDate.monthValue))
            pickerDate = pickerDate.withDayOfMonth(this.getInt(R.styleable.MaterialDatePickerView_defaultDay, pickerDate.dayOfMonth))

            viewCenter.setBackgroundColor(highlighterColor)
            viewCenter.layoutParams.height = highlighterHeight.toInt()

            if (background is ColorDrawable) {
                val backgroundColor = background.color
                viewTopShadeYear.setBackgroundColor(backgroundColor)
                viewTopShadeMonth.setBackgroundColor(backgroundColor)
                viewTopShadeTimeDay.setBackgroundColor(backgroundColor)
                viewBottomShadeYear.setBackgroundColor(backgroundColor)
                viewBottomShadeMonth.setBackgroundColor(backgroundColor)
                viewBottomShadeTimeDay.setBackgroundColor(backgroundColor)
                setBackgroundColor(backgroundColor)
            }
        }.recycle()
    }

    private var dateFormat = DateFormat.DD_MMM_YYYY
    private var textAppearance: Int = R.style.TextAppearance_MaterialDatePicker
    private var pickerDate: LocalDate = LocalDate.now()
    private var onDatePickedListener: OnDatePickedListener? = null

    private var yearsRange = (1950..2100)
    private var years = yearsRange.mapIndexed { index, value -> DateModel.Year(index, String.format("%02d", value), value) }
    private var months = (1..12).mapIndexed { index, value -> DateModel.Month(index, getMonthDisplayText(value), value) }
    private var days = (1..31).mapIndexed { index, value -> DateModel.Day(index, String.format("%02d", value), value) }

    private val yearAdapter = PickerAdapter(years, textAppearance) { position -> onItemClicked(position, rvYears) }
    private val monthAdapter = PickerAdapter(months, textAppearance) { position -> onItemClicked(position, rvMonths) }
    private val dayAdapter = PickerAdapter(days, textAppearance) { position -> onItemClicked(position, rvDays) }

    private val yearSnapHelper = LinearSnapHelper()
    private val monthSnapHelper = LinearSnapHelper()
    private val daySnapHelper = LinearSnapHelper()

    internal fun onTimePicked() {
        onDatePickedListener?.onDatePicked(getDate())
    }

    private fun initDateSelectionView() {
        rvYears.apply {
            setHasFixedSize(true)
            adapter = yearAdapter
            layoutManager = SlowLinearLayoutManager(context, rvYears){scrollToYear()}
            yearSnapHelper.attachToRecyclerView(this)
            addListeners { viewId -> updateDateWhenScroll(viewId) }
        }

        rvMonths.apply {
            setHasFixedSize(true)
            adapter = monthAdapter
            layoutManager = SlowLinearLayoutManager(context, rvMonths){scrollToMonth()}
            monthSnapHelper.attachToRecyclerView(this)
            addListeners { viewId -> updateDateWhenScroll(viewId) }
        }

        rvDays.apply {
            setHasFixedSize(true)
            adapter = dayAdapter
            layoutManager = SlowLinearLayoutManager(context, rvDays){scrollToDay()}
            daySnapHelper.attachToRecyclerView(this)
            addListeners { viewId -> updateDateWhenScroll(viewId) }
        }
    }

    private fun updateYearsAdapter() {
        years = yearsRange.mapIndexed { index, value -> DateModel.Year(index, String.format("%02d", value), value) }
        yearAdapter.updateItems(years)
    }

    private fun updateMonthsAdapter() {
        months = (1..12).mapIndexed { index, value -> DateModel.Month(index, getMonthDisplayText(value), value) }
        monthAdapter.updateItems(months)
    }

    private fun updateDaysAdapter() {
        days = (1..pickerDate.month.length(pickerDate.isLeapYear)).mapIndexed { index, value -> DateModel.Day(index, String.format("%02d", value), value) }
        dayAdapter.updateItems(days)
    }

    override fun fadeView(view: RecyclerView, duration: Long, alpha: Float) {
        when (view.id) {
            R.id.rvYears -> super.startFadeAnimation(listOf(viewTopShadeYear, viewBottomShadeYear), duration, alpha)
            R.id.rvMonths -> super.startFadeAnimation(listOf(viewTopShadeMonth, viewBottomShadeMonth), duration, alpha)
            R.id.rvDays -> super.startFadeAnimation(listOf(viewTopShadeTimeDay, viewBottomShadeTimeDay), duration, alpha)
        }
    }

    private fun updateDateWhenScroll(viewId: Int) {
        when (viewId) {
            R.id.rvYears -> {
                pickerDate = pickerDate.withYear(getYear())
                if (pickerDate.month.length(pickerDate.isLeapYear) != days.size) {
                    updateDaysAdapter()
                    scrollToDay()
                }
            }

            R.id.rvMonths -> {
                pickerDate = pickerDate.withMonth(getMonth())
                if (pickerDate.month.length(pickerDate.isLeapYear) != days.size) {
                    updateDaysAdapter()
                    scrollToDay()
                }
            }

            R.id.rvDays -> {
                pickerDate = pickerDate.withDayOfMonth(getDayOfMonth())
            }
        }
    }

    /**
     * Return selected date as a Long value
     *
     * @return
     */
    fun getDate(): Long = pickerDate.toLong()

    /**
     * Return selected day of month
     *
     * @return
     */
    fun getDayOfMonth(): Int {
        val view = daySnapHelper.findSnapView(rvDays.layoutManager) ?: return 0
        return days[(rvDays.getChildAdapterPosition(view) % days.size)].day
    }

    /**
     * Return selected month
     *
     * @return
     */
    fun getMonth(): Int {
        val view = monthSnapHelper.findSnapView(rvMonths.layoutManager) ?: return 0
        return months[(rvMonths.getChildAdapterPosition(view) % months.size)].month
    }

    /**
     * Return selected year
     *
     * @return
     */
    fun getYear(): Int {
        val view = yearSnapHelper.findSnapView(rvYears.layoutManager) ?: return 0
        return years[(rvYears.getChildAdapterPosition(view) % years.size)].year
    }

    private fun scrollToDate() {
        scrollToYear()
        scrollToMonth()
        scrollToDay()
    }

    private fun getMonthDisplayText(month: Int): String {
        return when (dateFormat) {
            DateFormat.DD_MMMM_YYYY -> {
                Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
            }
            DateFormat.DD_MMM_YYYY -> {
                Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }
            DateFormat.DD_MM_YYYY -> {
                String.format("%02d", month)
            }
        }
    }

    private fun scrollToYear() = rvYears.scrollToPosition(getScrollPosition(yearAdapter, years, getYearModel(pickerDate.year)))

    private fun scrollToMonth() = rvMonths.scrollToPosition(getScrollPosition(monthAdapter, months, getMonthModel(pickerDate.monthValue)))

    private fun scrollToDay() = rvDays.scrollToPosition(getScrollPosition(dayAdapter, days, getDayModel(pickerDate.dayOfMonth)))

    private fun getYearModel(year: Int) = years.firstOrNull { it.year == year }
        ?: throw MaterialDateTimePickerException("Cannot find given Year in given years range (size: ${years.size} index: $year)")

    private fun getMonthModel(month: Int) = months.firstOrNull { it.month == month }
        ?: throw MaterialDateTimePickerException("Cannot find given Month in given months range (size: ${months.size} index: $month)")

    private fun getDayModel(day: Int) = days.firstOrNull { it.day == day }
        ?: throw MaterialDateTimePickerException("Cannot find given Day in given days range (size: ${days.size} index: $day)")


    /**
     * Set callback listener in order to get the selected time
     * any event such as button click
     *
     * @param onDatePickedListener
     */
    fun setOnTimePickedListener(onDatePickedListener: OnDatePickedListener?) {
        this.onDatePickedListener = onDatePickedListener
    }

    /**
     * Set selected time. Given time should be in between MAX and MIN year range
     *
     * @param date
     */
    fun setDate(date: Long) {
        pickerDate = date.toLocalDate()
        scrollToDate()
    }

    /**
     * Set preferred time format to be displayed.
     * [DateFormat.DD_MMMM_YYYY] or [DateFormat.DD_MMM_YYYY] or [DateFormat.DD_MM_YYYY]
     *
     * @param dateFormat
     */
    fun setDateFormat(dateFormat: DateFormat) {
        this.dateFormat = dateFormat
        updateMonthsAdapter()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState: Parcelable? = super.onSaveInstanceState()
        superState?.let {
            val state = SavedState(superState)
            state.selectedYear = pickerDate.year
            state.selectedMonth = pickerDate.monthValue
            state.selectedDayOfMonth = pickerDate.dayOfMonth
            return state
        } ?: run {
            return superState
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                pickerDate=pickerDate.withYear(state.selectedYear).withMonth(state.selectedMonth).withDayOfMonth(state.selectedDayOfMonth)
            }
            else -> {
                super.onRestoreInstanceState(state)
            }
        }
    }

    internal class SavedState : BaseSavedState {
        var selectedYear: Int = OffsetDateTime.now().year
        var selectedMonth: Int = OffsetDateTime.now().monthValue
        var selectedDayOfMonth: Int = OffsetDateTime.now().dayOfMonth

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            selectedYear = source.readInt()
            selectedMonth = source.readInt()
            selectedDayOfMonth = source.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(selectedYear)
            out.writeInt(selectedMonth)
            out.writeInt(selectedDayOfMonth)
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

    fun interface OnDatePickedListener {
        fun onDatePicked(date: Long)
    }

    enum class DateFormat {
        DD_MMMM_YYYY, DD_MMM_YYYY, DD_MM_YYYY
    }
}
