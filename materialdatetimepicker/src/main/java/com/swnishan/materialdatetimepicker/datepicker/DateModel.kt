package com.swnishan.materialdatetimepicker.datepicker

import com.swnishan.materialdatetimepicker.common.PickerModel

sealed class DateModel {
    data class Year(
        override val index: Int,
        override val displayValue: String,
        val year: Int,
    ) : DateModel(), PickerModel

    data class Month(
        override val index: Int,
        override val displayValue: String,
        val month: Int
    ) : DateModel(), PickerModel

    data class Day(
        override val index: Int,
        override val displayValue: String,
        val day: Int
    ) : DateModel(), PickerModel
}