package com.example.health_butler


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_ingestion.*
import kotlinx.android.synthetic.main.fragment_ingestion.*
import kotlinx.android.synthetic.main.fragment_ingestion.go_nutrient
import kotlinx.android.synthetic.main.fragment_nutrient.*


class IngestionActivity : Fragment(){
    private var progr = 0
    val params1 = arrayOf("one", "two", "three")
    var i : Int = params1.size - 1

    var flag : Int = 0

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

//        progr = 70


        resetFragment()
//        updateProgressBar()
//        updateChartView()

//        go_nutrient.setOnClickListener{
//            val transaction = getChildFragmentManager().beginTransaction()
//            transaction.replace(R.id.statistics_layout, NutrientFragment())
//            transaction.commit()
//        }
//        go_ingestion.setOnClickListener{
//            val transaction = getChildFragmentManager().beginTransaction()
//            transaction.replace(R.id.statistics_layout, TotalIngestionFragment())
//            transaction.commit()
//            Log.v("aaa", "sucess")
//        }


    }

    fun resetFlag(num : Int){
        flag = num
        resetFragment()
    }

    fun resetFragment() {
        // 根据flag改变子fragment布局
        when (flag)
        {
            0 -> {
                val transaction = getChildFragmentManager().beginTransaction()
                transaction.replace(R.id.statistics_layout, TotalIngestionFragment())
                transaction.commit()
            }

            1 -> {
                val transaction = getChildFragmentManager().beginTransaction()
                transaction.replace(R.id.statistics_layout, NutrientFragment())
                transaction.commit()
            }
        }
    }

//    private fun updateProgressBar() {
//        ingestion_progress_bar.progress = progr
//        text_view_progress.text = "$progr%"
//    }

//    private fun updateChartView() {
//        val xvalues = ArrayList<String >()
//        xvalues.add("Coal")
//        xvalues.add("Petrolium")
//        xvalues.add("Natural Gas")
//
//        val piechartentry = ArrayList<Entry>()
//        piechartentry.add( Entry(23.5f, 0 ))
//        piechartentry.add( Entry(45.5f, 1 ))
//        piechartentry.add( Entry(68.5f, 2 ))
//
//        val piedataset = PieDataSet(piechartentry,"")
//        val data = PieData( xvalues,piedataset)
//
//        chart_view.setData(data)
//        piedataset.setColors(ColorTemplate.JOYFUL_COLORS)
//        piedataset.setSliceSpace(2f)
//        piedataset.setValueTextColor(Color.WHITE)
//        piedataset.setValueTextSize(10f)
//        piedataset.setSliceSpace(5f)
//    }
}