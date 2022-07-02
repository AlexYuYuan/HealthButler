package com.example.health_butler

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_ingestion.*
import kotlinx.android.synthetic.main.fragment_nutrient.*


class NutrientFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nutrient, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val xvalues = ArrayList<String >()
        xvalues.add("CHO")
        xvalues.add("PR")
        xvalues.add("Lipids")

        val piechartentry = ArrayList<Entry>()
        piechartentry.add( Entry(23.5f, 0 ))
        piechartentry.add( Entry(45.5f, 1 ))
        piechartentry.add( Entry(68.5f, 2 ))

        val piedataset = PieDataSet(piechartentry,"")
        val data = PieData( xvalues,piedataset)

        chart_view.setData(data)
        piedataset.setColors(ColorTemplate.JOYFUL_COLORS)
        piedataset.setSliceSpace(2f)
        piedataset.setValueTextColor(Color.WHITE)
        piedataset.setValueTextSize(10f)
        piedataset.setSliceSpace(5f)

        go_ingestion.setOnClickListener {
            val transaction = getFragmentManager()?.beginTransaction()
            transaction?.replace(R.id.statistics_layout, TotalIngestionFragment())
            transaction?.commit()
        }

        fun updateChart_view(date: String){
            val dietRecord = queryDietRecord(date!!.toInt())
            if (dietRecord != null) {
                piechartentry.clear()
                piechartentry.add( Entry(dietRecord.carbohydrate.toFloat(), 0 ))
                piechartentry.add( Entry(dietRecord.protein.toFloat(), 1 ))
                piechartentry.add( Entry(dietRecord.fat.toFloat(), 2 ))
                val data = PieData( xvalues,piedataset)
                chart_view.setData(data)
                chart_view.notifyDataSetChanged()
                chart_view.invalidate()
                nutrientTotalIntake.text = "三大营养素供能比例"
            }
            else{
                piechartentry.clear()
                piechartentry.add( Entry(0.0f, 0 ))
                piechartentry.add( Entry(0.0f, 1 ))
                piechartentry.add( Entry(0.0f, 2 ))
                val data = PieData( xvalues,piedataset)
                chart_view.setData(data)
                chart_view.notifyDataSetChanged()
                chart_view.invalidate()
                nutrientTotalIntake.text = "三大营养素供能比例（无记录）"
            }
        }

        updateChart_view(getDate().toString())

        parentFragmentManager.setFragmentResult("getDate", Bundle().apply {})

        parentFragmentManager.setFragmentResultListener("changeIngestion", this, FragmentResultListener { requestKey, result ->
            //事件处理
            val date = result.getString("date")
            updateChart_view(date!!)
        })
    }
}