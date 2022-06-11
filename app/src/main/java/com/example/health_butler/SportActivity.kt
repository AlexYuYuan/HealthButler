package com.example.health_butler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_sport.*

class SportActivity : Fragment() {
    private var progr = 0
    var compeleted_sportT = 30
    var total_sportT = 70
    val params1 = arrayOf("one", "two", "three")
    var i : Int = params1.size - 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_sport, container, false)
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

        completed.text = "30"
        total_sportTime.text = "70"
        progr = 60
        updateProgressBar()
    }

    private fun updateProgressBar() {
        sport_progress_bar.progress = progr
    }
}