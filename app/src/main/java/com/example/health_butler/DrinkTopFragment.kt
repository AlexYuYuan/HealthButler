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
    val params1 = arrayOf("one", "two", "three")
    var i : Int = params1.size - 1

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
            val result = Bundle()
            result.putString("date", getDate().toString())
            parentFragmentManager.setFragmentResult("type", result)
        }

        iv_calendar_next.setOnClickListener {
            parentFragmentManager.setFragmentResult("type", Bundle().apply {
                putString("date", getDate().toString())
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