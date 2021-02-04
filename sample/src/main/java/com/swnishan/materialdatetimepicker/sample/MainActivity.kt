package com.swnishan.materialdatetimepicker.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
// import com.google.android.material.timepicker.MaterialTimePicker
// import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_1.*
import kotlinx.android.synthetic.main.activity_main_1.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_1)

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, this.lifecycle)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Time Picker"
                1 -> tab.text = "Date Picker"
            }
        }.attach()

//        btnTimePickerDialog.setOnClickListener {
//            val builder = MaterialTimePickerDialog.Builder.setTitle("Set start time")
//                .setNegativeButtonText("Cancel")
//                .setPositiveButtonText("Ok")
//                .setTimeConvention(MaterialTimePickerView.TimeConvention.HOURS_12).setHour(0)
//                .setMinute(20).setTheme(R.style.TimePickerStyle2)
//                .build()
//
//            builder.setOnTimePickListener {
//                Toast.makeText(
//                    this,
//                    builder.getHour().toString(),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            builder.show(supportFragmentManager, MaterialTimePickerDialog::class.simpleName)
//
//
//            btnDatePickerDialog.setOnClickListener {
//                val builder = MaterialDatePickerDialog.Builder.setTitle("Set start date")
//                    .setNegativeButtonText("Cancel")
//                    .setPositiveButtonText("Ok").setDate(1664754120000)
//                    .setTheme(R.style.TimePickerStyle2)
//                    .setDateFormat(MaterialDatePickerView.DateFormat.DD_MMM_YYYY)
//                    .build()
//                builder.setOnDatePickListener { date ->
//                    Toast.makeText(
//                        it.context,
//                        date.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                builder.show(supportFragmentManager, MaterialTimePickerDialog::class.simpleName)
//            }
//        }
    }
}
