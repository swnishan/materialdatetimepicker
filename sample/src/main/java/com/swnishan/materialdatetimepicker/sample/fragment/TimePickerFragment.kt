package com.swnishan.materialdatetimepicker.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.swnishan.materialdatetimepicker.sample.R
import com.swnishan.materialdatetimepicker.timepicker.MaterialTimePickerDialog
import com.swnishan.materialdatetimepicker.timepicker.MaterialTimePickerView
import kotlinx.android.synthetic.main.fragment_time_picker.*

class TimePickerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnTimePickerDialog.setOnClickListener {
            val builder = MaterialTimePickerDialog.Builder.setTitle(getString(R.string.set_start_time))
                .setNegativeButtonText(getString(R.string.cancel))
                .setPositiveButtonText(getString(R.string.ok))
                // Below values can be set from the style as well (materialTimePickerViewStyle)
                .setTimeConvention(MaterialTimePickerView.TimeConvention.HOURS_12) // default 12 hours
                .setHour(13) // default current hour
                .setMinute(34) // default current minute
                .setTimePeriod(MaterialTimePickerView.TimePeriod.AM) // default based on the current time
                .setFadeAnimation(350L, 1050L, .3f, .7f)
                .setTheme(R.style.ThemeOverlay_Dialog_TimePicker) // default [R.style.ThemeOverlay_Dialog_MaterialTimePicker]
                .build()

            builder.setOnTimePickListener {selectedTime -> //Selected time as long value
                Toast.makeText(
                    requireContext(),
                    "${builder.getHour()} : ${builder.getMinute()} ${builder.getTimePeriod().name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.show(this.parentFragmentManager, MaterialTimePickerDialog::class.simpleName)
        }
    }
}
