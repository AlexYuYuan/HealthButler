package com.example.health_butler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_ingestion.*

class IngestionActivity : Fragment(){
    private var progr = 0
    val params1 = arrayOf("one", "two", "three")
    var i : Int = params1.size - 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_ingestion, container, false)
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

        progr = 70
        updateProgressBar()
    }

    private fun updateProgressBar() {
        progress_bar.progress = progr
        text_view_progress.text = "$progr%"
    }
}