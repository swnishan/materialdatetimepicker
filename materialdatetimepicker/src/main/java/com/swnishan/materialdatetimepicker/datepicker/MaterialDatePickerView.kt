package com.swnishan.materialdatetimepicker.datepicker

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.*
import com.swnishan.materialdatetimepicker.common.adapter.PickerAdapter
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView
import com.swnishan.materialdatetimepicker.timepicker.MaterialTimePickerView
import kotlinx.android.synthetic.main.view_date_picker.view.*
import kotlinx.android.synthetic.main.view_date_picker.view.viewCenter
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.format.TextStyle
import java.util.*

class MaterialDatePickerView: BaseMaterialDateTimePickerView{

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
    ){
        setCustomAttributes(attributeSet, defAttributeSet, R.style.Widget_MaterialDatePicker)
        initDateSelectionView()
        updateYearsAdapter()
        updateMonthsAdapter()
        updateDaysAdapter()
        scrollToDate()
    }

    init {
        inflate(context, R.layout.view_date_picker, this)
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        setPadding(0,Utils.dimenToPx(context,R.dimen.time_picker_view_padding_top),0, Utils.dimenToPx(context,R.dimen.time_picker_view_padding_bottom))
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

            dateFormat = DateFormat.values()[this.getInt(
                R.styleable.MaterialDatePickerView_dateFormat,
                0
            )]

            val minYear=this.getInt(R.styleable.MaterialDatePickerView_minYear, 1950)
            val maxYear=this.getInt(R.styleable.MaterialDatePickerView_maxYear, 2100)
            yearsRange=(minYear..maxYear)

            val year=this.getInt(R.styleable.MaterialDatePickerView_defaultYear, pickerDate.year)
            if(year<minYear || year>maxYear) throw MaterialDateTimePickerException("Default year ($year) out of the year range. It should be in between $minYear to $maxYear")

            pickerDate=pickerDate.withYear(year)
            pickerDate=pickerDate.withMonth(this.getInt(R.styleable.MaterialDatePickerView_defaultMonth, pickerDate.monthValue))
            pickerDate=pickerDate.withDayOfMonth(this.getInt(R.styleable.MaterialDatePickerView_defaultDay, pickerDate.dayOfMonth))

            viewCenter.setBackgroundColor(highlighterColor)
            viewCenter.layoutParams.height=highlighterHeight.toInt()

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

    private var dateFormat=DateFormat.DD_MMMM_YYYY
    private var textAppearance:Int= R.style.TextAppearance_MaterialDatePicker
    private var pickerDate: LocalDate = LocalDate.now()
    private var onDatePickedListener: OnDatePickedListener? = null

    private var yearsRange=(1950..2100)
    private var years = yearsRange.mapIndexed { index, value ->  DateModel.Year(index, String.format("%02d", value),value)}
    private var months = (1..12).mapIndexed { index, value ->  DateModel.Month(index, getMonthDisplayText(value), value)}
    private var days = (1..31).mapIndexed { index, value ->  DateModel.Day(index, String.format("%02d", value),value)}

    private val yearAdapter = PickerAdapter(years, textAppearance){position-> onItemClicked(position, rvYears) }
    private val monthAdapter = PickerAdapter(months, textAppearance){position-> onItemClicked(position, rvMonths) }
    private val dayAdapter = PickerAdapter(days, textAppearance){position-> onItemClicked(position, rvDays) }

    private val yearSnapHelper = LinearSnapHelper()
    private val monthSnapHelper = LinearSnapHelper()
    private val daySnapHelper = LinearSnapHelper()

    internal fun onTimePicked() {
        onDatePickedListener?.onDatePicked(pickerDate.toLong())
    }

    fun getDate():Long{
        val hourView=yearSnapHelper.findSnapView(rvYears.layoutManager)?:return 0
        return rvYears.getChildAdapterPosition(hourView).toLong()
    }

    private fun initDateSelectionView() {
        rvYears.apply {
            setHasFixedSize(true)
            adapter = yearAdapter
            layoutManager = SlowLinearLayoutManager(context)
            yearSnapHelper.attachToRecyclerView(this)
            addListeners()
        }

        rvMonths.apply {
            setHasFixedSize(true)
            adapter = monthAdapter
            layoutManager = SlowLinearLayoutManager(context)
            monthSnapHelper.attachToRecyclerView(this)
            addListeners()
        }

        rvDays.apply {
            setHasFixedSize(true)
            adapter= dayAdapter
            layoutManager=SlowLinearLayoutManager(context)
            daySnapHelper.attachToRecyclerView(this)
            addListeners()
        }
    }

    private fun updateYearsAdapter(){
        years=yearsRange.mapIndexed { index, value ->  DateModel.Year(index, String.format("%02d", value),value)}
        yearAdapter.updateItems(years)
    }

    private fun updateMonthsAdapter(){
        months = (1..12).mapIndexed { index, value ->  DateModel.Month(index, getMonthDisplayText(value), value)}
        monthAdapter.updateItems(months)
    }

    private fun updateDaysAdapter() {
        days=(1..pickerDate.month.length(pickerDate.isLeapYear)).mapIndexed { index, value ->  DateModel.Day(index, String.format("%02d", value),value)}
        dayAdapter.updateItems(days)
    }

    private fun animateShadeView(view: View, duration: Long, alpha: Float) {
        when(view.id){
            R.id.rvYears -> super.animateShadeView(listOf(viewTopShadeYear, viewBottomShadeYear),duration, alpha)
            R.id.rvMonths -> super.animateShadeView(listOf(viewTopShadeMonth, viewBottomShadeMonth),duration, alpha)
            R.id.rvDays -> super.animateShadeView(listOf(viewTopShadeTimeDay, viewBottomShadeTimeDay),duration, alpha)
        }
    }

    private fun RecyclerView.addListeners(){
        addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == ACTION_DOWN) animateShadeView(rv, 300, .2f)
                else if (e.action == ACTION_UP && rv.scrollState == SCROLL_STATE_IDLE) animateShadeView(
                    rv,
                    1000,
                    .7f
                )
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    SCROLL_STATE_DRAGGING -> animateShadeView(recyclerView, 300, .3f)
                    SCROLL_STATE_IDLE -> animateShadeView(recyclerView, 1000, .7f)
                }

                if(newState==SCROLL_STATE_IDLE){
                    when(recyclerView){
                        rvYears->{
                            pickerDate=pickerDate.withYear(getYear())
                            if(pickerDate.month.length(pickerDate.isLeapYear)!=days.size){
                                updateDaysAdapter()
                                scrollToDay()
                            }
                        }

                        rvMonths->{
                            pickerDate=pickerDate.withMonth(getMonth())
                            if(pickerDate.month.length(pickerDate.isLeapYear)!=days.size) {
                                updateDaysAdapter()
                                scrollToDay()
                            }
                        }

                        rvDays->{
                            pickerDate=pickerDate.withDayOfMonth(getDayOfMonth())
                        }
                    }
                }
            }
        })
    }

    fun getDayOfMonth(): Int {
        val view = daySnapHelper.findSnapView(rvDays.layoutManager) ?: return 0
        return days[(rvDays.getChildAdapterPosition(view) % days.size)].day
    }

    fun getMonth(): Int {
        val view=monthSnapHelper.findSnapView(rvMonths.layoutManager)?:return 0
        return months[(rvMonths.getChildAdapterPosition(view)%months.size)].month
    }

    fun getYear(): Int {
        val view=yearSnapHelper.findSnapView(rvYears.layoutManager)?:return 0
        return years[(rvYears.getChildAdapterPosition(view)%years.size)].year
    }

    private fun scrollToDate() {
        scrollToYear()
        scrollToMonth()
        scrollToDay()
    }

    private fun getMonthDisplayText(month:Int): String {
        return when(dateFormat){
            DateFormat.DD_MMMM_YYYY->{
                Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
            }
            DateFormat.DD_MMM_YYYY->{
                Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }
            DateFormat.DD_MM_YYYY->{
                String.format("%02d", month)
            }
        }
    }

    private fun scrollToYear()=rvYears.scrollToPosition(getScrollPosition(yearAdapter,years, getYearModel(pickerDate.year)))

    private fun scrollToMonth()=rvMonths.scrollToPosition(getScrollPosition(monthAdapter,months, getMonthModel(pickerDate.monthValue)))

    private fun scrollToDay()=rvDays.scrollToPosition(getScrollPosition(dayAdapter,days, getDayModel(pickerDate.dayOfMonth)))

    private fun getYearModel(year: Int) = years.firstOrNull { it.year == year }
        ?: throw MaterialDateTimePickerException("Cannot find given Year in given years range (size: ${years.size} index: $year)")

    private fun getMonthModel(month: Int) = months.firstOrNull { it.month == month }
        ?: throw MaterialDateTimePickerException("Cannot find given Month in given months range (size: ${months.size} index: $month)")

    private fun getDayModel(day: Int) = days.firstOrNull { it.day == day }
        ?: throw MaterialDateTimePickerException("Cannot find given Day in given days range (size: ${days.size} index: $day)")

    fun setOnTimePickedListener(onDatePickedListener: OnDatePickedListener?) {
        this.onDatePickedListener = onDatePickedListener
    }

    fun setDate(date:Long) {
        pickerDate = date.toLocalDate()
        scrollToDate()
    }

    fun setDateFormat(dateFormat: DateFormat){
        this.dateFormat=dateFormat
        updateMonthsAdapter()
    }

    fun interface OnDatePickedListener {
        fun onDatePicked(date: Long)
    }

    enum class DateFormat{
        DD_MMMM_YYYY, DD_MMM_YYYY, DD_MM_YYYY
    }
}