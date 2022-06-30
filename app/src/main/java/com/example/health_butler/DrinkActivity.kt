package com.example.health_butler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_drink.*
import kotlinx.android.synthetic.main.fragment_drink_buttom_left.*
import kotlinx.android.synthetic.main.fragment_drink_top.*

class DrinkActivity : Fragment() {

    var i : Int = 0
    var dateI : Int = getDate() + i * 86400
    var dateS : String = getDateFormat(dateI)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_drink, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tv_date.text = "今天"

        iv_calendar_next.setOnClickListener {
            i++
            dateI = getDate() + i * 86400
            dateS = getDateFormat(dateI)
            if (i == 0) {
                dateS = "今天"
            }
            tv_date.text = dateS
        }
        iv_calendar_previous.setOnClickListener {
            i--
            dateI = getDate() + i * 86400
            dateS = getDateFormat(dateI)
            if (i == 0) {
                dateS = "今天"
            }
            tv_date.text = dateS
        }

        drinkWater.setOnClickListener {
            val transaction = getChildFragmentManager().beginTransaction()
            transaction.replace(R.id.buttom_layout, DrinkButtomLeftFragment())
            transaction.commit()
        }
        remind.setOnClickListener {
            val transaction = getChildFragmentManager().beginTransaction()
            transaction.replace(R.id.buttom_layout, DrinkButtomRightFragment())
            transaction.commit()
        }
    }
}