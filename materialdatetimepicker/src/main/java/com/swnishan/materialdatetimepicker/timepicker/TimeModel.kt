package com.swnishan.materialdatetimepicker.timepicker

import com.swnishan.materialdatetimepicker.common.PickerModel


sealed class TimeModel {
    data class Hour(
        override val index: Int,
        override val displayValue: String,
        val hour: Int,
    ) : TimeModel(), PickerModel

    data class Minute(
        override val index: Int,
        override val displayValue: String,
        val minute: Int
    ) : TimeModel(), PickerModel

    data class TimePeriod(
        override val index: Int,
        override val displayValue: String,
        val timePeriod: Int
    ) : TimeModel(), PickerModel
}