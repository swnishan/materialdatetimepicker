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
            val builder = MaterialTimePickerDialog.Builder.setTitle("Set start time")
                .setNegativeButtonText("Cancel")
                .setPositiveButtonText("Ok")
                .setTimeConvention(MaterialTimePickerView.TimeConvention.HOURS_12)
                .setTheme(R.style.ThemeOverlay_Dialog_TimePicker)
                .build()

            builder.setOnTimePickListener {
                Toast.makeText(
                    requireContext(),
                    builder.getHour().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.show(this.parentFragmentManager, MaterialTimePickerDialog::class.simpleName)
        }
    }
}
