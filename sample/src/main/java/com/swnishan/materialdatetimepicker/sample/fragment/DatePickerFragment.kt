package com.swnishan.materialdatetimepicker.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.swnishan.materialdatetimepicker.datepicker.MaterialDatePickerDialog
import com.swnishan.materialdatetimepicker.datepicker.MaterialDatePickerView
import com.swnishan.materialdatetimepicker.sample.R
import com.swnishan.materialdatetimepicker.sample.databinding.FragmentDatePickerBinding
import org.threeten.bp.OffsetDateTime

class DatePickerFragment : Fragment() {
    private var binding: FragmentDatePickerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDatePickerBinding.inflate(inflater, container, false)
        return requireNotNull(binding).root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        requireNotNull(binding).btnDatePickerDialog.setOnClickListener {
            val builder =
                MaterialDatePickerDialog.Builder
                    .setTitle(getString(R.string.set_start_date))
                    .setNegativeButtonText(getString(R.string.cancel))
                    .setPositiveButtonText(getString(R.string.ok))
                    // Below values can be set from the style as well (materialDatePickerViewStyle)
                    .setDate(
                        OffsetDateTime
                            .now()
                            .plusDays(10)
                            .toInstant()
                            .toEpochMilli(),
                    ) // default current date
                    .setDateFormat(MaterialDatePickerView.DateFormat.DD_MMMM_YYYY) // default DateFormat.DD_MMM_YYYY (05 Feb 2021)
                    .setTheme(R.style.ThemeOverlay_Dialog_DatePicker) // default R.style.ThemeOverlay_Dialog_MaterialDatePicker
                    .setFadeAnimation(350L, 1050L, .3f, .7f)
                    .build()

            builder.setOnDatePickListener { selectedDate ->
                // selected date as long value
                Toast
                    .makeText(
                        it.context,
                        "${builder.getDayOfMonth()}-${builder.getMonth()}-${builder.getYear()}",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            builder.show(requireNotNull(this.fragmentManager), MaterialDatePickerDialog::class.simpleName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
