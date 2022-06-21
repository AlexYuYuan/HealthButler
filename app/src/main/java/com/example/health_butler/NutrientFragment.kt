package com.example.health_butler

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
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
        xvalues.add("Coal")
        xvalues.add("Petrolium")
        xvalues.add("Natural Gas")

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
            //调用父fragment中的resetFlag方法改变flag
        }
    }
}