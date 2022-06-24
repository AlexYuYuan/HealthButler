package com.example.health_butler

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_drink_buttom_right.*
import java.util.*
import kotlin.collections.ArrayList


class DrinkButtomRightFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drink_buttom_right, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        var allData : ArrayList<String> = arrayListOf("17:00", "19:00", "20:00")
//        val adapter = ArrayAdapter<String>(this.requireContext(), R.layout.fragment_drink_buttom_right, allData)
//        showData.adapter = adapter
        
        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                Log.v("aaa", "bbb")
            }
            else {
                Log.v("aaa", "ccc")
            }
        }

        add.setOnClickListener {
            val _timePickerDialog : TimePickerDialog
            val hourOfDay = 2
            val minute = 2
            val is24HourView = true
            var hourS = ""
            var minuteS = ""

            _timePickerDialog = TimePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog,
                OnTimeSetListener { timePicker, i, i1 -> //*Return values
                    if(i < 10) {
                        hourS = "0" + "$i"
                    }
                    else {
                        hourS = "$i"
                    }
                    if(i1 < 10) {
                        minuteS = "0" + "$i1"
                    }
                    else {
                        minuteS = "$i1"
                    }
                    setTime.setText("$hourS:$minuteS")
                }, hourOfDay, minute, is24HourView
            )
            _timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _timePickerDialog.setTitle("Select a Time")
            _timePickerDialog.show()
        }
    }
}