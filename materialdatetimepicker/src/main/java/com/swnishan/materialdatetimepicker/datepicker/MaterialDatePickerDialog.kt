package com.swnishan.materialdatetimepicker.datepicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.annotation.AttrRes
import androidx.annotation.FloatRange
import androidx.annotation.StyleRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.dialog.BaseMaterialDateTimePickerDialog
import com.swnishan.materialdatetimepicker.common.toLocalDate
import com.swnishan.materialdatetimepicker.common.toLong
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_IN_ALPHA
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_IN_DURATION
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_OUT_ALPHA
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_OUT_DURATION
import org.threeten.bp.*

class MaterialDatePickerDialog : BaseMaterialDateTimePickerDialog() {

    private var onDatePickedListener: MaterialDatePickerView.OnDatePickedListener? = null
    private var materialDatePickerView: MaterialDatePickerView? = null
    private var pickerDate: LocalDate = LocalDate.now()
    private var themeRes= R.style.ThemeOverlay_Dialog_MaterialDatePicker
    private var dateFormat= MaterialDatePickerView.DateFormat.DD_MMMM_YYYY

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext(), themeRes)
        val datePickerViewStyle = builder.context.resolveThemeAttr(R.attr.materialTimePickerViewStyle)
        val datePickerThemeContext = ContextThemeWrapper(builder.context, datePickerViewStyle)
        materialDatePickerView = MaterialDatePickerView(context=datePickerThemeContext)
        materialDatePickerView?.setDate(pickerDate.toLong())
        materialDatePickerView?.setDateFormat(dateFormat)
        materialDatePickerView?.setFadeAnimation(fadeInDuration, fadeOutDuration, fadeInAlpha, fadeOutAlpha)
        materialDatePickerView?.setOnTimePickedListener(onDatePickedListener)

        builder.apply {
            setView(materialDatePickerView)
            setTitle(arguments?.getString(ARG_TITLE) ?: "")
            setNegativeButton(arguments?.getString(ARG_NEGATIVE_BUTTON_TEXT), null)
            setPositiveButton(arguments?.getString(ARG_POSITIVE_BUTTON_TEXT)) { _, _ -> materialDatePickerView?.onTimePicked() }
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

        val date =bundle.getLong(ARG_DATE,pickerDate.toLong())
        pickerDate=date.toLocalDate()

        this.dateFormat=MaterialDatePickerView.DateFormat.valueOf(bundle.getString(ARG_DATE_FORMAT, dateFormat.name))

        fadeInDuration=bundle.getLong(ARG_FADE_IN_DURATION, fadeInDuration)
        fadeOutDuration=bundle.getLong(ARG_FADE_OUT_DURATION, fadeOutDuration)
        fadeInAlpha=bundle.getFloat(ARG_FADE_IN_ALPHA, fadeInAlpha)
        fadeOutAlpha=bundle.getFloat(ARG_FADE_OUT_ALPHA, fadeOutAlpha)

    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        bundle.putInt(ARG_THEME, themeRes)
        bundle.putLong(ARG_DATE, materialDatePickerView?.getDate()?:pickerDate.toLong())
        bundle.putString(ARG_DATE_FORMAT, dateFormat.name)
        bundle.putLong(ARG_FADE_IN_DURATION, fadeInDuration)
        bundle.putLong(ARG_FADE_OUT_DURATION, fadeOutDuration)
        bundle.putFloat(ARG_FADE_IN_ALPHA, fadeInAlpha)
        bundle.putFloat(ARG_FADE_IN_ALPHA, fadeOutAlpha)
    }

     override fun setStyle(style: Int, theme: Int) {
        themeRes=theme
        super.setStyle(style, theme)
    }

    fun getDate()=materialDatePickerView?.getDate()?: throw ExceptionInInitializerError("Material date picker view not been initialized")

    fun getYear()=materialDatePickerView?.getYear()?: throw ExceptionInInitializerError("Material date picker view not been initialized")

    fun getMonth()=materialDatePickerView?.getMonth()?: throw ExceptionInInitializerError("Material date picker view not been initialized")

    fun getDayOfMonth()=materialDatePickerView?.getDayOfMonth()?: throw ExceptionInInitializerError("Material date picker view not been initialized")

    fun setOnDatePickListener(listener: MaterialDatePickerView.OnDatePickedListener?) {
         onDatePickedListener=listener
    }

    object Builder{
        private val timePickerDialog=MaterialDatePickerDialog()
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

        fun setDate(date:Long): Builder {
            bundle.putLong(ARG_DATE, date)
            return this
        }

        fun setDateFormat(dateFormat: MaterialDatePickerView.DateFormat): Builder {
            bundle.putString(ARG_DATE_FORMAT, dateFormat.name)
            return this
        }

        fun setTheme(@StyleRes themeRes:Int): Builder {
            bundle.putInt(ARG_THEME, themeRes)
            return this
        }

        fun setFadeAnimation(
            fadeInDuration: Long=FADE_IN_DURATION,
            fadeOutDuration: Long=FADE_OUT_DURATION,
            @FloatRange(from = 0.0, to = 1.0) fadeInAlpha: Float= FADE_IN_ALPHA,
            @FloatRange(from = 0.0, to = 1.0) fadeOutAlpha: Float= FADE_OUT_ALPHA
        ): Builder {
            bundle.putLong(ARG_FADE_IN_DURATION, fadeInDuration)
            bundle.putLong(ARG_FADE_OUT_DURATION, fadeOutDuration)
            bundle.putFloat(ARG_FADE_IN_ALPHA, fadeInAlpha)
            bundle.putFloat(ARG_FADE_OUT_ALPHA, fadeOutAlpha)
            return this
        }

        fun build():MaterialDatePickerDialog{
            timePickerDialog.apply { arguments=bundle }
            return timePickerDialog
        }
    }

    companion object {
        private const val ARG_POSITIVE_BUTTON_TEXT = "arg_positive_button_text"
        private const val ARG_NEGATIVE_BUTTON_TEXT = "arg_negative_button_text"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_THEME = "arg_theme"
        private const val ARG_DATE = "arg_date"
        private const val ARG_DATE_FORMAT = "arg_date_format"
    }

}
