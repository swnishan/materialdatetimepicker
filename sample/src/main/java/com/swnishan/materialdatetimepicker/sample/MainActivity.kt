package com.swnishan.materialdatetimepicker.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.swnishan.materialdatetimepicker.timepicker.TimePickerDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show.setOnClickListener {
            TimePickerDialog.create("set", "Ok", "Cancel", null).show(supportFragmentManager, "")
        }
    }
}