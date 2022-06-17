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

    val params1 = arrayOf("one", "two", "three")
    var i : Int = params1.size - 1

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

        tv_date.text = params1[i]

        iv_calendar_next.setOnClickListener {
            if (i < params1.size - 1) {
                i++
            }
            tv_date.text = params1[i]
        }
        iv_calendar_previous.setOnClickListener {
            if (i > 0) {
                i--
            }
            tv_date.text = params1[i]
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