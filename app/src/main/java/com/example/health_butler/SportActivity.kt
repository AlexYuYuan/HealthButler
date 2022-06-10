package com.example.health_butler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_ingestion.*
import kotlinx.android.synthetic.main.activity_ingestion.progress_bar
import kotlinx.android.synthetic.main.activity_sport.*

class SportActivity : Fragment() {
    private var progr = 0
    private var compeleted_sportT = 30
    private var total_sportT = 70

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_sport, container, false)
        return view
    }
    val progress_bar = view?.findViewById<ProgressBar>(R.id.progress_bar)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        completed.text = "30"
        total_sportTime.text = "70"
        progr = compeleted_sportT / total_sportT
        updateProgressBar()
    }

    private fun updateProgressBar() {
        progress_bar?.progress = progr
        text_view_progress.text = "$progr%"
    }
}