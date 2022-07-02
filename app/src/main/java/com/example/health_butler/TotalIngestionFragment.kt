package com.example.health_butler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
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

        var progr = 0.0
        fun updateBar(date: String) {
            val dietRecord = queryDietRecord(date!!.toInt())
            if (dietRecord != null) {

                progr = (dietRecord.calorie.toDouble()/2500.0) * 100
                ingestion_progress_bar.progress = progr.toInt()
                text_view_progress.text = "$progr%"
            } else {
                ingestion_progress_bar.progress = 0
                text_view_progress.text = "无记录"
            }
        }

        updateBar(getDate().toString())

        go_nutrient.setOnClickListener {
            val transaction = getFragmentManager()?.beginTransaction()
            transaction?.replace(R.id.statistics_layout, NutrientFragment())
            transaction?.commit()
        }

        parentFragmentManager.setFragmentResult("getDate", Bundle().apply {})

        parentFragmentManager.setFragmentResultListener("changeIngestion", this, FragmentResultListener { requestKey, result ->
            //事件处理
            val date = result.getString("date")
            updateBar(date!!)
        })
    }
}