package com.swnishan.materialdatetimepicker.sample.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.swnishan.materialdatetimepicker.datepicker.MaterialDatePickerDialog
import com.swnishan.materialdatetimepicker.datepicker.MaterialDatePickerView
import com.swnishan.materialdatetimepicker.sample.R
import com.swnishan.materialdatetimepicker.timepicker.MaterialTimePickerDialog
import kotlinx.android.synthetic.main.fragment_date_picker.*
import org.threeten.bp.OffsetDateTime


class DatePickerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDatePickerDialog.setOnClickListener {
            val builder = MaterialDatePickerDialog.Builder.setTitle("Set start date")
                .setNegativeButtonText("Cancel")
                .setPositiveButtonText("Ok")
//                .setDate(OffsetDateTime.now().toInstant().toEpochMilli())
                .setTheme(R.style.ThemeOverlay_Dialog_DatePicker)
                .setDateFormat(MaterialDatePickerView.DateFormat.DD_MMM_YYYY)
                .build()

            builder.setOnDatePickListener { date ->
                Toast.makeText(
                    it.context,
                    date.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
            builder.show(this.parentFragmentManager, MaterialDatePickerDialog::class.simpleName)
        }
    }
}