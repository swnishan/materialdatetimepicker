package com.swnishan.materialdatetimepicker.timepicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.annotation.AttrRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.dialog.BaseMaterialDateTimePickerDialog
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_IN_ALPHA
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_IN_DURATION
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_OUT_ALPHA
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_OUT_DURATION
import com.swnishan.materialdatetimepicker.datepicker.MaterialDatePickerDialog
import org.threeten.bp.LocalTime

class MaterialTimePickerDialog : BaseMaterialDateTimePickerDialog() {

    private var onTimePickedListener: MaterialTimePickerView.OnTimePickedListener? = null
    private var materialTimePickerView: MaterialTimePickerView? = null
    private var timeConvention = MaterialTimePickerView.TimeConvention.HOURS_24
    private var timePeriod: MaterialTimePickerView.TimePeriod = MaterialTimePickerView.TimePeriod.AM
    private var pickerTime: LocalTime = LocalTime.now()
    private var themeRes= R.style.ThemeOverlay_Dialog_MaterialTimePicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext(), themeRes)
        val timePickerViewStyle = builder.context.resolveThemeAttr(R.attr.materialTimePickerViewStyle)
        val timePickerThemeContext = ContextThemeWrapper(builder.context, timePickerViewStyle)
        materialTimePickerView = MaterialTimePickerView(context=timePickerThemeContext)
        materialTimePickerView?.setTimeConvention(timeConvention)
        materialTimePickerView?.setHour(pickerTime.hour)
        materialTimePickerView?.setMinute(pickerTime.minute)
        materialTimePickerView?.setFadeAnimation(fadeInDuration, fadeOutDuration, fadeInAlpha, fadeOutAlpha)
        materialTimePickerView?.setOnTimePickedListener(onTimePickedListener)

        builder.apply {
            setView(materialTimePickerView)
            setTitle(arguments?.getString(ARG_TITLE) ?: "")
            setNegativeButton(arguments?.getString(ARG_NEGATIVE_BUTTON_TEXT), null)
            setPositiveButton(arguments?.getString(ARG_POSITIVE_BUTTON_TEXT)) { _, _ -> materialTimePickerView?.onTimePicked() }
        }

        return builder.create()
    }

    private fun Context.resolveThemeAttr(@AttrRes attr: Int): Int = TypedValue().let { typedValue ->
        theme.resolveAttribute(attr, typedValue, true)
        typedValue.resourceId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreState(savedInstanceState?:arguments)
    }

    private fun restoreState(bundle: Bundle?) {
        if (bundle==null) return
        themeRes=bundle.getInt(ARG_THEME, themeRes)

        val hour =bundle.getInt(ARG_HOUR, pickerTime.hour)
        val minute=bundle.getInt(ARG_MINUTE, pickerTime.minute)
        pickerTime=pickerTime.withHour(hour).withMinute(minute)

        timeConvention= MaterialTimePickerView.TimeConvention.valueOf(bundle.getString(ARG_TIME_CONVENTION, timeConvention.name))
        timePeriod= MaterialTimePickerView.TimePeriod.valueOf(bundle.getString(ARG_TIME_PERIOD, timePeriod.name))

        fadeInDuration=bundle.getLong(ARG_FADE_IN_DURATION, fadeInDuration)
        fadeOutDuration=bundle.getLong(ARG_FADE_OUT_DURATION, fadeOutDuration)
        fadeInAlpha=bundle.getFloat(ARG_FADE_IN_ALPHA, fadeInAlpha)
        fadeOutAlpha=bundle.getFloat(ARG_FADE_OUT_ALPHA, fadeOutAlpha)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        bundle.putInt(ARG_THEME, themeRes)
        bundle.putInt(ARG_HOUR, materialTimePickerView?.getHour()?:pickerTime.hour)
        bundle.putInt(ARG_MINUTE, materialTimePickerView?.getMinute()?:pickerTime.minute)
        bundle.putString(ARG_TIME_CONVENTION, timeConvention.name)
        bundle.putString(ARG_TIME_PERIOD, timePeriod.name)
        bundle.putLong(ARG_FADE_IN_DURATION, fadeInDuration)
        bundle.putLong(ARG_FADE_OUT_DURATION, fadeOutDuration)
        bundle.putFloat(ARG_FADE_IN_ALPHA, fadeInAlpha)
        bundle.putFloat(ARG_FADE_IN_ALPHA, fadeOutAlpha)
    }

     override fun setStyle(style: Int, theme: Int) {
        themeRes=theme
        super.setStyle(style, theme)
    }

    fun getHour()=materialTimePickerView?.getHour()?: throw ExceptionInInitializerError("Material time picker view not been initialized")

    fun getMinute()=materialTimePickerView?.getMinute()?: throw ExceptionInInitializerError("Material time picker view not been initialized")

    fun getTimePeriod()=materialTimePickerView?.getTimePeriod()?: throw ExceptionInInitializerError("Material time picker view not been initialized")

    fun getTime()=materialTimePickerView?.getTime()?: throw ExceptionInInitializerError("Material time picker view not been initialized")

    fun setOnTimePickListener(listener: MaterialTimePickerView.OnTimePickedListener?) {
         onTimePickedListener=listener
    }

    object Builder{
        private val timePickerDialog=MaterialTimePickerDialog()
        private val bundle = bundleOf()

        fun setTitle(title:String): Builder {
            bundle.putString(ARG_TITLE, title)
            return this
        }

        fun setPositiveButtonText(text:String): Builder {
            bundle.putString(ARG_POSITIVE_BUTTON_TEXT, text)
            return this
        }

        fun setNegativeButtonText(text:String): Builder {
            bundle.putString(ARG_NEGATIVE_BUTTON_TEXT, text)
            return this
        }

        fun setTimeConvention(timeConvention: MaterialTimePickerView.TimeConvention): Builder {
            bundle.putString(ARG_TIME_CONVENTION, timeConvention.name)
            return this
        }

        fun setHour(@IntRange(from =0 ,to=23)hour: Int): Builder {
            bundle.putInt(ARG_HOUR, hour)
            return this
        }

        fun setMinute(@IntRange(from = 0, to = 60) minute: Int): Builder {
            bundle.putInt(ARG_MINUTE, minute)
            return this
        }

        fun setTimePeriod(timePeriod: MaterialTimePickerView.TimePeriod):Builder{
            bundle.putString(ARG_TIME_PERIOD,timePeriod.name)
            return this
        }

        fun setTheme(@StyleRes themeRes:Int): Builder {
            bundle.putInt(ARG_THEME, themeRes)
            return this
        }

        fun setFadeAnimation(
            fadeInDuration: Long= FADE_IN_DURATION,
            fadeOutDuration: Long= FADE_OUT_DURATION,
            @FloatRange(from = 0.0, to = 1.0) fadeInAlpha: Float= FADE_IN_ALPHA,
            @FloatRange(from = 0.0, to = 1.0) fadeOutAlpha: Float= FADE_OUT_ALPHA
        ): Builder {
            bundle.putLong(ARG_FADE_IN_DURATION, fadeInDuration)
            bundle.putLong(ARG_FADE_OUT_DURATION, fadeOutDuration)
            bundle.putFloat(ARG_FADE_IN_ALPHA, fadeInAlpha)
            bundle.putFloat(ARG_FADE_OUT_ALPHA, fadeOutAlpha)
            return this
        }

        fun build():MaterialTimePickerDialog{
            timePickerDialog.apply { arguments=bundle }
            return timePickerDialog
        }
    }

    companion object {
        private const val ARG_POSITIVE_BUTTON_TEXT = "arg_positive_button_text"
        private const val ARG_NEGATIVE_BUTTON_TEXT = "arg_negative_button_text"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_THEME = "arg_theme"
        private const val ARG_TIME_CONVENTION = "arg_time_convention"
        private const val ARG_HOUR = "arg_hour"
        private const val ARG_MINUTE = "arg_minute"
        private const val ARG_TIME_PERIOD = "arg_time_period"
    }

}
