package com.swnishan.materialdatetimepicker.datepicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.common.toLocalDate
import com.swnishan.materialdatetimepicker.common.toLong
import org.threeten.bp.*

class MaterialDatePickerDialog : DialogFragment() {

    private var onDatePickedListener: MaterialDatePickerView.OnDatePickedListener? = null
    private var materialDatePickerView: MaterialDatePickerView? = null
    private var pickerDate: LocalDate = LocalDate.now()
    private var themeRes= R.style.ThemeOverlay_Dialog_MaterialDatePicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext(), themeRes)
        val datePickerViewStyle = builder.context.resolveThemeAttr(R.attr.materialTimePickerViewStyle)
        val datePickerThemeContext = ContextThemeWrapper(builder.context, datePickerViewStyle)
        materialDatePickerView = MaterialDatePickerView(context=datePickerThemeContext)
        materialDatePickerView?.setDate(pickerDate.toLong())
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

    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        bundle.putInt(ARG_THEME, themeRes)
        bundle.putLong(ARG_DATE, materialDatePickerView?.getDate()?:pickerDate.toLong())
    }

     override fun setStyle(style: Int, theme: Int) {
        themeRes=theme
        super.setStyle(style, theme)
    }

    fun getDate()=materialDatePickerView?.getDate()?: throw ExceptionInInitializerError("Material date picker view not been initialized")

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

        fun setTheme(@StyleRes themeRes:Int): Builder {
            bundle.putInt(ARG_THEME, themeRes)
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
    }

}
