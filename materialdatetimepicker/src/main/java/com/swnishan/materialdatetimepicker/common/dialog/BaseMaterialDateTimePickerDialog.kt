package com.swnishan.materialdatetimepicker.common.dialog

import androidx.fragment.app.DialogFragment
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_IN_ALPHA
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_IN_DURATION
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_OUT_ALPHA
import com.swnishan.materialdatetimepicker.common.view.BaseMaterialDateTimePickerView.Companion.FADE_OUT_DURATION

open class BaseMaterialDateTimePickerDialog : DialogFragment(){

    internal var fadeInDuration= FADE_IN_DURATION
    internal var fadeOutDuration= FADE_OUT_DURATION
    internal var fadeInAlpha= FADE_IN_ALPHA
    internal var fadeOutAlpha= FADE_OUT_ALPHA

    companion object{
        const val ARG_FADE_IN_DURATION = "arg_fade_in_duration"
        const val ARG_FADE_OUT_DURATION = "arg_fade_out_duration"
        const val ARG_FADE_IN_ALPHA = "arg_fade_in_alpha"
        const val ARG_FADE_OUT_ALPHA = "arg_fade_out_alpha"
    }

}