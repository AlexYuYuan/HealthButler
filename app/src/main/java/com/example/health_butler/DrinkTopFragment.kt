package com.example.health_butler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_drink_buttom_left.*
import kotlinx.android.synthetic.main.fragment_drink_top.*
import kotlin.math.log

class DrinkTopFragment : Fragment() {
    var i : Int = 0
    var dateI : Int = getDate() + i * 86400
    var dateS : String = getDateFormat(dateI)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_drink_top, container, false)

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        iv_calendar_previous.setOnClickListener {
            i--
            dateI = getDate() + i * 86400
            dateS = getDateFormat(dateI)
            if (i == 0) {
                dateS = "今天"
            }
            tv_date.text = dateS
            parentFragmentManager.setFragmentResult("changeDate", Bundle().apply {
                putString("date", dateI.toString())
            })
        }
        iv_calendar_next.setOnClickListener {
            if (i == 0) {
                dateS = "今天"
            }
            else{
                i++
                if(i == 0) {
                    dateS = "今天"
                    dateI = getDate() + i * 86400
                }
                else {
                    dateI = getDate() + i * 86400
                    dateS = getDateFormat(dateI)
                }
            }
            tv_date.text = dateS
            parentFragmentManager.setFragmentResult("changeDate", Bundle().apply {
                putString("date", dateI.toString())
            })
        }
//        super.onActivityCreated(savedInstanceState)
//
//        tv_date.text = params1[i]
//
//        iv_calendar_next.setOnClickListener {
//            if (i < params1.size - 1) {
//                i++
//            }
//            tv_date.text = params1[i]
//        }
//        iv_calendar_previous.setOnClickListener {
//            if (i > 0) {
//                i--
//            }
//            tv_date.text = params1[i]
//        }
//
//        val transaction = getFragmentManager()?.beginTransaction()
//        drinkWater.setOnClickListener {
//            transaction?.replace(R.id.buttom_layout, DrinkButtomLeftFragment())
//            transaction?.commit()
//        }
//        remind.setOnClickListener {
//            transaction?.replace(R.id.buttom_layout, DrinkButtomRightFragment())
//            transaction?.commit()
//        }
    }
}