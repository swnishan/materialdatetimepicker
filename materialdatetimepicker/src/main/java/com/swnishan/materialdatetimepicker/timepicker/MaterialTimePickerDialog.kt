package com.swnishan.materialdatetimepicker.timepicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.annotation.AttrRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.swnishan.materialdatetimepicker.R
import com.swnishan.materialdatetimepicker.timepicker.view.MaterialTimePickerView

class MaterialTimePickerDialog : DialogFragment() {

    private var onTimePickedListener: MaterialTimePickerView.OnTimePickedListener? = null
    private var materialTimePickerView: MaterialTimePickerView? = null
    private var clockType = MaterialTimePickerView.TimeConvention.HOURS_24
    private var themeRes= R.style.ThemeOverlay_Dialog_MaterialTimePicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext(), themeRes)
        val timePickerViewStyle = builder.context.resolveThemeAttr(R.attr.materialTimePickerViewStyle)
        val timePickerThemeContext = ContextThemeWrapper(builder.context, timePickerViewStyle)
        materialTimePickerView = MaterialTimePickerView(context=timePickerThemeContext)
        materialTimePickerView?.setTimeConvention(clockType)
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

    fun setClockType(timeConvention: MaterialTimePickerView.TimeConvention){
        this.clockType = timeConvention
    }

    override fun setStyle(style: Int, theme: Int) {
        themeRes=theme
        super.setStyle(style, theme)
    }

    fun setOnTimePickListener(listener:MaterialTimePickerView.OnTimePickedListener?) {
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

        fun setClockType(timeConvention: MaterialTimePickerView.TimeConvention): Builder {
            timePickerDialog.setClockType(timeConvention)
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
    }

}
