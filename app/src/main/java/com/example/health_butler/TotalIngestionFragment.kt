package com.example.health_butler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_ingestion.*

class TotalIngestionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingestion, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var progr = 70
        ingestion_progress_bar.progress = progr
        text_view_progress.text = "$progr%"

        go_nutrient.setOnClickListener {
            val transaction = getFragmentManager()?.beginTransaction()
            transaction?.replace(R.id.statistics_layout, NutrientFragment())
            transaction?.commit()
        }
    }
}