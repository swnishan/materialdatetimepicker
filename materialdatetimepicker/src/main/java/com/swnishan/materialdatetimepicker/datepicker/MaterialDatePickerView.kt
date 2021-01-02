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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.PickerModel
import com.swnishan.materialdatetimepicker.common.Utils
import com.swnishan.materialdatetimepicker.common.toLocalDate
import com.swnishan.materialdatetimepicker.common.toLong
import com.swnishan.materialdatetimepicker.common.adapter.PickerAdapter
import kotlinx.android.synthetic.main.view_date_picker.view.*
import org.threeten.bp.LocalDate
import kotlin.math.absoluteValue

class MaterialDatePickerView: ConstraintLayout{

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
                60f
            )

            val background = this.getDrawable(
                R.styleable.MaterialTimePickerView_android_background
            )

            textAppearance = this.getResourceId(
                R.styleable.MaterialDatePickerView_android_textAppearance,
                R.style.TextAppearance_MaterialTimePicker
            )

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

    private var textAppearance:Int= R.style.TextAppearance_MaterialTimePicker
    private val years = (1950..2100).mapIndexed { index, value ->  DateModel.Year(index, String.format("%02d", value),value)}
    private val months = (1..12).mapIndexed { index, value ->  DateModel.Month(index, String.format("%02d", value),value)}
    private val days = (0..31).mapIndexed { index, value ->  DateModel.Day(index, String.format("%02d", value),value)}

    private val yearAdapter = PickerAdapter(years, textAppearance)
    private val monthAdapter = PickerAdapter(months, textAppearance)
    private val dayAdapter = PickerAdapter(days, textAppearance)

    private val yearSnapHelper = LinearSnapHelper()
    private val monthSnapHelper = LinearSnapHelper()
    private val daySnapHelper = LinearSnapHelper()

    private var pickerDate: LocalDate = LocalDate.now()
    private var onDatePickedListener: OnDatePickedListener? = null

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
            layoutManager = LinearLayoutManager(context)
            yearSnapHelper.attachToRecyclerView(this)
            addListeners()
        }

        rvMonths.apply {
            setHasFixedSize(true)
            adapter = monthAdapter
            layoutManager = LinearLayoutManager(context)
            monthSnapHelper.attachToRecyclerView(this)
            addListeners()
        }

        rvDays.apply {
            setHasFixedSize(true)
            adapter= dayAdapter
            layoutManager=LinearLayoutManager(context)
            daySnapHelper.attachToRecyclerView(this)
            addListeners()
        }
    }

    private fun animateShadeView(view: View, duration: Long, alpha: Float): Boolean {
        when(view.id){
            R.id.rvYears -> {
                listOf(viewTopShadeYear, viewBottomShadeYear).forEach {
                    it.animate().alpha(alpha).setDuration(duration)
                        .withEndAction { it.alpha = alpha }.start()
                }
            }
            R.id.rvMonths -> {
                listOf(viewTopShadeMonth, viewBottomShadeMonth).forEach {
                    it.animate().alpha(alpha).setDuration(duration)
                        .withEndAction { it.alpha = alpha }.start()
                }
            }
            R.id.rvDays -> {
                listOf(viewTopShadeTimeDay, viewBottomShadeTimeDay).forEach {
                    it.animate().alpha(alpha).setDuration(duration)
                        .withEndAction { it.alpha = alpha }.start()
                }
            }
        }
        return true
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
            }
        })
    }

    private fun scrollToDate() {
        rvYears.scrollToPosition(getScrollPosition(yearAdapter,years, getYear(pickerDate.year)))
        rvMonths.scrollToPosition(getScrollPosition(monthAdapter,months, getMonth(pickerDate.monthValue)))
        rvDays.scrollToPosition(getScrollPosition(dayAdapter,days, getDay(pickerDate.dayOfMonth)))
    }

    private fun getYear(year: Int) = years.firstOrNull { it.year == year }
        ?: throw ArrayIndexOutOfBoundsException("Cannot find given Year in given years range (size: ${years.size} index: $year)")

    private fun getMonth(month: Int) = months.firstOrNull { it.month == month }
        ?: throw ArrayIndexOutOfBoundsException("Cannot find given Month in given months range (size: ${months.size} index: $month)")

    private fun getDay(day: Int) = days.firstOrNull { it.day == day }
        ?: throw ArrayIndexOutOfBoundsException("Cannot find given Day in given days range (size: ${days.size} index: $day)")

    /**
     * Here we get the scroll position with relative to middle position of list of items
     * since we set the adapter count as Int.MAX_VALUE
     */
    private fun getScrollPosition(adapter: PickerAdapter, list: List<PickerModel>, model: PickerModel): Int {
        var scrollPosition = adapter.itemCount/2
        val position = scrollPosition % list.size

        val diff = (model.index - position).absoluteValue
        if (model.index > position) scrollPosition += diff else scrollPosition -= diff

        return scrollPosition
    }

    fun setOnTimePickedListener(onDatePickedListener: OnDatePickedListener?) {
        this.onDatePickedListener = onDatePickedListener
    }

    fun setDate(date:Long) {
        pickerDate = date.toLocalDate()
        scrollToDate()
    }

    interface OnDatePickedListener {
        fun onDatePicked(date: Long)
    }

}