package com.swnishan.materialdatetimepicker.common.dialog

import androidx.fragment.app.DialogFragment

open class BaseMaterialDateTimePickerDialog : DialogFragment(){

    internal var fadeInDuration=300L
    internal var fadeOutDuration=1000L
    internal var fadeInAlpha=.3f
    internal var fadeOutAlpha=.7f

    companion object{
        const val ARG_FADE_IN_DURATION = "arg_fade_in_duration"
        const val ARG_FADE_OUT_DURATION = "arg_fade_out_duration"
        const val ARG_FADE_IN_ALPHA = "arg_fade_in_alpha"
        const val ARG_FADE_OUT_ALPHA = "arg_fade_out_alpha"
    }

}